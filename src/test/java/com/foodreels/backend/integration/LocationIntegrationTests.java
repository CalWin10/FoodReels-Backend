package com.foodreels.backend.integration;

import com.foodreels.backend.support.BackendIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LocationIntegrationTests extends BackendIntegrationTest {
    @Test
    void nearbyRestaurants_shouldExcludeOutsideRadiusAndSortNearestFirst() throws Exception {
        var near = restaurant(owner, "Nearby", 11.01, 77.0);
        // Inside the bounding rectangle but outside the circular 5 km radius.
        restaurant(owner, "Outside circle", 11.04, 77.04);
        em.flush();
        mvc.perform(get("/api/location/restaurants/nearby").with(as(customer))
                        .param("lat", "11").param("lng", "77").param("radiusKm", "5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].restaurant.id").value(restaurant.getId()))
                .andExpect(jsonPath("$[0].distanceKm").value(0.0))
                .andExpect(jsonPath("$[1].restaurant.id").value(near.getId()))
                .andExpect(jsonPath("$[1].distanceKm").value(1.11));
    }

    @Test
    void nearbyFoodsAndReels_shouldUseRestaurantCoordinates() throws Exception {
        var near = restaurant(owner, "Nearby", 11.01, 77.0);
        var nearFood = food(near, "Nearby meal", "RICE", 50);
        var farFood = food(otherRestaurant, "Far meal", "RICE", 60);
        var closeReel = reel(food, "Close reel", LocalDateTime.now(), 0);
        var nearReel = reel(nearFood, "Nearby reel", LocalDateTime.now(), 0);
        reel(farFood, "Far reel", LocalDateTime.now(), 0);
        mvc.perform(get("/api/location/foods/nearby").with(as(customer)).param("lat", "11").param("lng", "77"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].food.id").value(food.getId()))
                .andExpect(jsonPath("$[1].food.id").value(nearFood.getId()))
                .andExpect(jsonPath("$[1].distanceKm").value(1.11));
        mvc.perform(get("/api/location/reels/nearby").with(as(customer)).param("lat", "11").param("lng", "77"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reel.id").value(closeReel.getId()))
                .andExpect(jsonPath("$[1].reel.id").value(nearReel.getId()))
                .andExpect(jsonPath("$[1].distanceKm").value(1.11));
    }

    @Test
    void combinedDiscovery_shouldReturnRestaurantsFoodsAndReels() throws Exception {
        var reel = reel(food, "Local reel", LocalDateTime.now(), 0);
        mvc.perform(get("/api/location/nearby").with(as(customer)).param("lat", "11").param("lng", "77"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.restaurants", hasSize(1)))
                .andExpect(jsonPath("$.foods[0].food.id").value(food.getId()))
                .andExpect(jsonPath("$.reels[0].reel.id").value(reel.getId()));
    }

    @ParameterizedTest
    @CsvSource({"91,77,5", "-91,77,5", "11,181,5", "11,-181,5", "11,77,0", "11,77,-1", "11,77,101"})
    void location_shouldRejectInvalidCoordinatesAndRadius(String lat, String lng, String radius) throws Exception {
        mvc.perform(get("/api/location/nearby").with(as(customer))
                        .param("lat", lat).param("lng", lng).param("radiusKm", radius))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/location/nearby"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"lat", "lng"})
    void location_shouldRequireBothCoordinates(String supplied) throws Exception {
        mvc.perform(get("/api/location/nearby").with(as(customer)).param(supplied, "11"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/restaurants/nearby", "/foods/nearby", "/reels/nearby"})
    void individualDiscoveryEndpoints_shouldValidateCoordinates(String route) throws Exception {
        mvc.perform(get("/api/location" + route).with(as(customer)).param("lat", "91").param("lng", "77"))
                .andExpect(status().isBadRequest());
    }
}
