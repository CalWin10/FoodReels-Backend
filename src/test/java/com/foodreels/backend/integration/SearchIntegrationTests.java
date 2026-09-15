package com.foodreels.backend.integration;

import com.foodreels.backend.support.BackendIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SearchIntegrationTests extends BackendIntegrationTest {
    @ParameterizedTest
    @CsvSource({"margherita", "oven"})
    void searchFoods_shouldMatchNameAndDescription(String query) throws Exception {
        food(otherRestaurant, "Salad", "SALAD", 75).setDescription("Raw vegetables");
        em.flush();
        mvc.perform(get("/api/search/foods").with(as(customer)).param("q", query))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(food.getId()));
    }

    @ParameterizedTest
    @CsvSource({"category,pizza", "minPrice,100", "maxPrice,150"})
    void searchFoods_shouldApplyCategoryAndPriceFilters(String parameter, String value) throws Exception {
        double otherPrice = parameter.equals("minPrice") ? 50 : 200;
        food(otherRestaurant, "Other meal", "RICE", otherPrice);
        em.flush();
        mvc.perform(get("/api/search/foods").with(as(customer)).param(parameter, value))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(food.getId()));
    }

    @Test
    void searchFoods_shouldFilterRestaurantAndPaginate() throws Exception {
        var second = food(restaurant, "Second food", "PIZZA", 150);
        food(otherRestaurant, "Excluded", "PIZZA", 90);
        em.flush();
        mvc.perform(get("/api/search/foods").with(as(customer))
                        .param("restaurantId", restaurant.getId().toString()).param("size", "1")
                        .param("page", "1").param("sort", "price_asc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(second.getId()));
    }

    @ParameterizedTest
    @CsvSource({"price_asc,true", "price_desc,false", "name,false", "oldest,true", "newest,false"})
    void searchFoods_shouldUseSupportedSortOrders(String sort, boolean originalFirst) throws Exception {
        var second = food(restaurant, "Apple", "FRUIT", 200);
        food.setCreatedAt(LocalDateTime.now().minusDays(2));
        em.flush();
        mvc.perform(get("/api/search/foods").with(as(customer)).param("sort", sort))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id")
                        .value(originalFirst ? food.getId() : second.getId()));
    }

    @ParameterizedTest
    @CsvSource({"Central", "Market"})
    void searchRestaurants_shouldMatchNameAndAddress(String query) throws Exception {
        otherRestaurant.setAddress("Distant Avenue");
        em.flush();
        mvc.perform(get("/api/search/restaurants").with(as(customer)).param("q", query))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(restaurant.getId()));
    }

    @Test
    void searchRestaurants_shouldFilterByRating() throws Exception {
        otherRestaurant.setRating(2.0);
        em.flush();
        mvc.perform(get("/api/search/restaurants").with(as(customer)).param("minRating", "4"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(restaurant.getId()));
    }

    @ParameterizedTest
    @CsvSource({"rating,true", "name,true", "oldest,true", "newest,false"})
    void searchRestaurants_shouldUseSupportedSortOrders(String sort, boolean originalFirst) throws Exception {
        restaurant.setRating(5.0);
        restaurant.setCreatedAt(LocalDateTime.now().minusDays(2));
        em.flush();
        mvc.perform(get("/api/search/restaurants").with(as(customer)).param("sort", sort))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id")
                        .value(originalFirst ? restaurant.getId() : otherRestaurant.getId()));
    }

    @Test
    void searchReels_shouldMatchCaption() throws Exception {
        var match = reel(food, "Stretchy cheese", LocalDateTime.now(), 0);
        reel(food, "Other caption", LocalDateTime.now(), 0);
        mvc.perform(get("/api/search/reels").with(as(customer)).param("q", "stretchy"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(match.getId()));
    }

    @ParameterizedTest
    @CsvSource({"category", "restaurantId", "foodId"})
    void searchReels_shouldApplyRelationshipFilters(String parameter) throws Exception {
        var match = reel(food, "Included", LocalDateTime.now(), 0);
        reel(food(otherRestaurant, "Rice", "RICE", 80), "Excluded", LocalDateTime.now(), 0);
        String value = switch (parameter) {
            case "category" -> "pizza";
            case "restaurantId" -> restaurant.getId().toString();
            default -> food.getId().toString();
        };
        mvc.perform(get("/api/search/reels").with(as(customer)).param(parameter, value))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(match.getId()));
    }

    @ParameterizedTest
    @CsvSource({"popular,true", "oldest,true", "newest,false"})
    void searchReels_shouldUseSupportedSortOrders(String sort, boolean oldFirst) throws Exception {
        var old = reel(food, "Older popular", LocalDateTime.now().minusDays(2), 100);
        var recent = reel(food, "Recent", LocalDateTime.now(), 0);
        mvc.perform(get("/api/search/reels").with(as(customer)).param("sort", sort))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id")
                        .value(oldFirst ? old.getId() : recent.getId()));
    }

    @Test
    void unifiedSearch_shouldReturnAllTypesAndRankWithinFetchedPage() throws Exception {
        food.setName("Pizza");
        food.setCreatedAt(LocalDateTime.now().minusDays(2));
        var weaker = food(restaurant, "Lunch", "RICE", 60);
        weaker.setDescription("Includes pizza");
        restaurant.setName("Pizza Kitchen");
        var reel = reel(food, "Pizza cooking", LocalDateTime.now(), 0);
        em.flush();
        mvc.perform(get("/api/search").with(as(customer)).param("q", "pizza").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalFoods").value(2))
                .andExpect(jsonPath("$.foods[0].id").value(food.getId()))
                .andExpect(jsonPath("$.restaurants[0].id").value(restaurant.getId()))
                .andExpect(jsonPath("$.reels[0].id").value(reel.getId()));
    }

    @Test
    void search_shouldReturnEmptyPageForNoMatches() throws Exception {
        mvc.perform(get("/api/search/foods").with(as(customer)).param("q", "no-such-food"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
