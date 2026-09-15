package com.foodreels.backend.support;

import java.time.LocalDateTime;
import java.util.UUID;

import com.foodreels.backend.entity.*;
import com.foodreels.backend.repository.*;
import com.foodreels.backend.service.PersonalizedFeedCacheService;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcPrint;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@ActiveProfiles("test")
@Transactional
public abstract class BackendIntegrationTest {
    @Autowired protected MockMvc mvc;
    @Autowired protected EntityManager em;
    @Autowired protected UserRepository users;
    @Autowired protected RestaurantRepository restaurants;
    @Autowired protected FoodRepository foods;
    @Autowired protected ReelRepository reels;
    @Autowired protected OrderRepository orders;
    @Autowired protected PasswordEncoder passwordEncoder;

    // Production defines an explicit Redis manager, so cache.type alone cannot replace it.
    @TestBean(methodName = "noOpCacheManager")
    CacheManager cacheManager;

    // This service uses Redis directly rather than Spring's cache abstraction.
    @MockitoBean
    PersonalizedFeedCacheService personalizedFeedCacheService;

    static CacheManager noOpCacheManager() {
        return new NoOpCacheManager();
    }

    protected User customer;
    protected User otherCustomer;
    protected User owner;
    protected User otherOwner;
    protected User admin;
    protected Restaurant restaurant;
    protected Restaurant otherRestaurant;
    protected Food food;

    @BeforeEach
    void createFixtures() {
        customer = user(UserRole.USER);
        otherCustomer = user(UserRole.USER);
        owner = user(UserRole.RESTAURANT_OWNER);
        otherOwner = user(UserRole.RESTAURANT_OWNER);
        admin = user(UserRole.ADMIN);
        restaurant = restaurant(owner, "Central Kitchen", 11.0, 77.0);
        otherRestaurant = restaurant(otherOwner, "Far Kitchen", 12.0, 78.0);
        food = food(restaurant, "Margherita", "PIZZA", 125.50);
        em.flush();
    }

    protected User user(UserRole role) {
        User user = new User();
        user.setName("Test " + role);
        user.setEmail(UUID.randomUUID() + "@example.test");
        user.setPassword(passwordEncoder.encode("Test-password-123"));
        user.setRole(role);
        return users.save(user);
    }

    protected Restaurant restaurant(User owner, String name, double lat, double lng) {
        Restaurant r = new Restaurant();
        r.setName(name);
        r.setDescription("Test restaurant");
        r.setAddress("Market Street");
        r.setPhoneNumber("1234567890");
        r.setLatitude(lat);
        r.setLongitude(lng);
        r.setRating(4.5);
        r.setOwner(owner);
        return restaurants.save(r);
    }

    protected Food food(Restaurant restaurant, String name, String category, double price) {
        Food f = new Food();
        f.setName(name);
        f.setDescription("Fresh oven baked meal");
        f.setCategory(category);
        f.setPrice(price);
        f.setRestaurant(restaurant);
        return foods.save(f);
    }

    protected Reel reel(Food food, String caption, LocalDateTime createdAt, long views) {
        Reel r = new Reel();
        r.setFood(food);
        r.setVideoUrl("https://example.test/video.mp4");
        r.setCaption(caption);
        r.setViewCount(views);
        reels.saveAndFlush(r);
        // @PrePersist initializes the timestamp; set the desired age after insertion.
        r.setCreatedAt(createdAt);
        em.flush();
        return r;
    }

    protected RequestPostProcessor as(User user) {
        return jwt().jwt(j -> j.subject(user.getEmail()).claim("role", user.getRole().name()))
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    protected String orderBody(long foodId, int quantity) {
        return """
                {"deliveryAddress":"Test Address","items":[{"foodId":%d,"quantity":%d}]}
                """.formatted(foodId, quantity);
    }

    protected long createOrder(User user, Food food) throws Exception {
        MvcResult result = mvc.perform(post("/api/orders").with(as(user))
                        .contentType(MediaType.APPLICATION_JSON).content(orderBody(food.getId(), 2)))
                .andExpect(status().isCreated()).andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }
}
