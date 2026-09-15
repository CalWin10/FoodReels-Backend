package com.foodreels.backend.security;

import com.foodreels.backend.support.BackendIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import java.time.Instant;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityRegressionTests extends BackendIntegrationTest {
    @Autowired JwtEncoder encoder;

    @Test
    void restaurantOwner_shouldNotManageOrdersWithoutAssignedRestaurantOwner() throws Exception {
        long id = createOrder(customer, food);
        restaurant.setOwner(null);
        em.flush();
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId()).with(as(owner)))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/api/orders/{id}/status", id).with(as(owner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ownerAndAdmin_shouldDeleteRestaurantsTheyCanManage() throws Exception {
        var ownEmpty = restaurant(owner, "Own empty", 11, 77);
        var otherEmpty = restaurant(otherOwner, "Other empty", 11, 77);
        mvc.perform(delete("/api/restaurants/{id}", ownEmpty.getId()).with(as(owner)))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/api/restaurants/{id}", otherEmpty.getId()).with(as(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void anonymousRequest_shouldReturnStandardized401() throws Exception {
        mvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message", not(emptyOrNullString())))
                .andExpect(jsonPath("$.path").value("/api/orders"));
    }

    @Test
    void invalidJwt_shouldReturnStandardized401() throws Exception {
        mvc.perform(get("/api/orders").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/orders"));
    }

    @Test
    void user_shouldAccessOwnOrderHistory() throws Exception {
        mvc.perform(get("/api/orders").with(as(customer))).andExpect(status().isOk());
    }

    @Test
    void wrongRole_shouldReturnStandardized403() throws Exception {
        String path = "/api/orders/restaurant/" + restaurant.getId();
        mvc.perform(get(path).with(as(customer)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message", not(emptyOrNullString())))
                .andExpect(jsonPath("$.path").value(path));
    }

    @Test
    void restaurantOwner_shouldAccessOwnRestaurantOrders() throws Exception {
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId()).with(as(owner)))
                .andExpect(status().isOk());
    }

    @Test
    void restaurantOwner_shouldNotAccessAnotherRestaurantOrders() throws Exception {
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId()).with(as(otherOwner)))
                .andExpect(status().isForbidden());
    }

    @Test
    void restaurantOwner_shouldNotUpdateAnotherRestaurantOrder() throws Exception {
        long id = createOrder(customer, food);
        mvc.perform(patch("/api/orders/{id}/status", id).with(as(otherOwner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isForbidden());
        org.assertj.core.api.Assertions.assertThat(orders.findById(id).orElseThrow().getStatus().name())
                .isEqualTo("CREATED");
    }

    @Test
    void restaurantOwner_shouldNotUpdateAnotherRestaurant() throws Exception {
        mvc.perform(put("/api/restaurants/{id}", restaurant.getId()).with(as(otherOwner))
                        .contentType(MediaType.APPLICATION_JSON).content(restaurantBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void restaurantOwner_shouldNotDeleteAnotherRestaurant() throws Exception {
        mvc.perform(delete("/api/restaurants/{id}", restaurant.getId()).with(as(otherOwner)))
                .andExpect(status().isForbidden());
        org.assertj.core.api.Assertions.assertThat(restaurants.existsById(restaurant.getId())).isTrue();
    }

    @Test
    void admin_shouldUpdateAnotherRestaurant() throws Exception {
        mvc.perform(put("/api/restaurants/{id}", restaurant.getId()).with(as(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(restaurantBody()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated Kitchen"));
    }

    @Test
    void admin_shouldAccessAndManageRestaurantOrders() throws Exception {
        long id = createOrder(customer, food);
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId()).with(as(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(id));
        mvc.perform(patch("/api/orders/{id}/status", id).with(as(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void signedJwt_shouldUseProductionRoleConverter() throws Exception {
        String token = token(owner.getEmail(), "RESTAURANT_OWNER", Instant.now().plusSeconds(300));
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId())
                        .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        String userToken = token(customer.getEmail(), "USER", Instant.now().plusSeconds(300));
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId())
                        .header("Authorization", "Bearer " + userToken)).andExpect(status().isForbidden());
    }

    @Test
    void expiredJwt_shouldReturn401() throws Exception {
        mvc.perform(get("/api/orders").header("Authorization", "Bearer " +
                        token(customer.getEmail(), "USER", Instant.now().minusSeconds(300))))
                .andExpect(status().isUnauthorized());
    }

    private String token(String email, String role, Instant expires) {
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder().subject(email).claim("role", role)
                        .issuedAt(Instant.now().minusSeconds(600)).expiresAt(expires).build())).getTokenValue();
    }

    private String restaurantBody() {
        return """
                {"name":"Updated Kitchen","description":"Updated","address":"New Street","phoneNumber":"1234567890"}
                """;
    }
}
