package com.foodreels.backend.integration;

import com.foodreels.backend.entity.OrderStatus;
import com.foodreels.backend.support.BackendIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderIntegrationTests extends BackendIntegrationTest {
    @Test
    void createOrder_shouldCalculateTotalFromDatabasePriceAndPersistItems() throws Exception {
        long id = createOrder(customer, food);
        em.flush();
        em.clear();
        var order = orders.findById(id).orElseThrow();
        assertThat(order.getUser().getId()).isEqualTo(customer.getId());
        assertThat(order.getRestaurant().getId()).isEqualTo(restaurant.getId());
        assertThat(order.getDeliveryAddress()).isEqualTo("Test Address");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("251.00");
        assertThat(order.getItems()).hasSize(1);
        var item = order.getItems().getFirst();
        assertThat(item.getId()).isNotNull();
        assertThat(item.getFood().getId()).isEqualTo(food.getId());
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualByComparingTo("125.50");
        assertThat(item.getSubtotal()).isEqualByComparingTo("251.00");
        food = foods.findById(food.getId()).orElseThrow();
        food.setPrice(999.0);
        em.flush();
        em.clear();
        assertThat(orders.findById(id).orElseThrow().getItems().getFirst().getUnitPrice())
                .isEqualByComparingTo("125.50");
    }

    @Test
    void createOrder_shouldIgnoreClientSuppliedPricesAndOwnership() throws Exception {
        mvc.perform(post("/api/orders").with(as(customer)).contentType(MediaType.APPLICATION_JSON).content("""
                {"deliveryAddress":"Test Address","userId":%d,"restaurantId":%d,"totalAmount":0.01,
                 "items":[{"foodId":%d,"quantity":2,"unitPrice":0.01,"subtotal":0.01}]}
                """.formatted(otherCustomer.getId(), otherRestaurant.getId(), food.getId())))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.userId").value(customer.getId()))
                .andExpect(jsonPath("$.restaurantId").value(restaurant.getId()))
                .andExpect(jsonPath("$.totalAmount").value(251.0))
                .andExpect(jsonPath("$.items[0].unitPrice").value(125.5))
                .andExpect(jsonPath("$.items[0].subtotal").value(251.0));
    }

    @Test
    void createOrder_shouldMergeDuplicateFoodItems() throws Exception {
        mvc.perform(post("/api/orders").with(as(customer)).contentType(MediaType.APPLICATION_JSON).content("""
                {"deliveryAddress":"Test Address","items":[{"foodId":%d,"quantity":2},{"foodId":%d,"quantity":3}]}
                """.formatted(food.getId(), food.getId())))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(5))
                .andExpect(jsonPath("$.totalAmount").value(627.5));
        em.flush();
        em.clear();
        assertThat(orders.findAll().getFirst().getItems()).hasSize(1);
    }

    @Test
    void createOrder_shouldRejectItemsFromDifferentRestaurants() throws Exception {
        var otherFood = food(otherRestaurant, "Other food", "RICE", 50);
        mvc.perform(post("/api/orders").with(as(customer)).contentType(MediaType.APPLICATION_JSON).content("""
                {"deliveryAddress":"Test Address","items":[{"foodId":%d,"quantity":2},{"foodId":%d,"quantity":3}]}
                """.formatted(food.getId(), otherFood.getId())))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message")
                        .value("All food items must belong to the same restaurant"));
        assertThat(orders.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createOrder_shouldRejectInvalidQuantity(int quantity) throws Exception {
        mvc.perform(post("/api/orders").with(as(customer)).contentType(MediaType.APPLICATION_JSON)
                        .content(orderBody(food.getId(), quantity))).andExpect(status().isBadRequest());
        assertThat(orders.count()).isZero();
    }

    @Test
    void createOrder_shouldRejectMissingFood() throws Exception {
        mvc.perform(post("/api/orders").with(as(customer)).contentType(MediaType.APPLICATION_JSON)
                        .content(orderBody(Long.MAX_VALUE, 1))).andExpect(status().isNotFound());
        assertThat(orders.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"deliveryAddress\":\"Test\",\"items\":[]}",
            "{\"deliveryAddress\":\"\",\"items\":[{\"foodId\":1,\"quantity\":1}]}"})
    void createOrder_shouldValidateRequiredFields(String body) throws Exception {
        mvc.perform(post("/api/orders").with(as(customer)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void getMyOrders_shouldIsolateUsersAndPaginateNewestFirst() throws Exception {
        long first = createOrder(customer, food);
        long second = createOrder(customer, food);
        createOrder(otherCustomer, food);
        mvc.perform(get("/api/orders").with(as(customer)).param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(second));
        mvc.perform(get("/api/orders").with(as(customer)).param("page", "1").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(first));
        mvc.perform(get("/api/orders").with(as(customer)).param("page", "2").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getMyOrders_shouldFilterByStatus() throws Exception {
        long delivered = createOrder(customer, food);
        orders.findById(delivered).orElseThrow().setStatus(OrderStatus.DELIVERED);
        createOrder(customer, food);
        mvc.perform(get("/api/orders").with(as(customer)).param("status", "DELIVERED"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(delivered));
    }

    @Test
    void getOrder_shouldRejectDifferentUser() throws Exception {
        long id = createOrder(customer, food);
        mvc.perform(get("/api/orders/{id}", id).with(as(otherCustomer))).andExpect(status().isForbidden());
        mvc.perform(get("/api/orders/{id}", id).with(as(customer))).andExpect(status().isOk());
    }

    @Test
    void cancelOrder_shouldAllowCreatedOrder() throws Exception {
        long id = createOrder(customer, food);
        mvc.perform(post("/api/orders/{id}/cancel", id).with(as(customer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED"));
        em.flush();
        em.clear();
        assertThat(orders.findById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_shouldRejectDifferentUser() throws Exception {
        long id = createOrder(customer, food);
        mvc.perform(post("/api/orders/{id}/cancel", id).with(as(otherCustomer)))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelOrder_shouldRejectConfirmedOrder() throws Exception {
        long id = createOrder(customer, food);
        update(id, "CONFIRMED", 200);
        mvc.perform(post("/api/orders/{id}/cancel", id).with(as(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_shouldAllowFullWorkflowAndRejectDeliveredTerminalTransition() throws Exception {
        long id = createOrder(customer, food);
        for (String status : new String[]{"CONFIRMED", "PREPARING", "READY", "OUT_FOR_DELIVERY", "DELIVERED"}) {
            update(id, status, 200);
            assertThat(orders.findById(id).orElseThrow().getStatus().name()).isEqualTo(status);
        }
        update(id, "PREPARING", 400);
        assertThat(orders.findById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void updateStatus_shouldRejectInvalidTransition() throws Exception {
        long id = createOrder(customer, food);
        update(id, "DELIVERED", 400);
        assertThat(orders.findById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void updateStatus_shouldRejectCancelledTerminalTransition() throws Exception {
        long id = createOrder(customer, food);
        mvc.perform(post("/api/orders/{id}/cancel", id).with(as(customer))).andExpect(status().isOk());
        update(id, "CONFIRMED", 400);
        assertThat(orders.findById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void updateStatus_shouldAllowSameStatus() throws Exception {
        long id = createOrder(customer, food);
        update(id, "CREATED", 200);
    }

    @Test
    void restaurantOrders_shouldFilterStatusAndPaginate() throws Exception {
        long first = createOrder(customer, food);
        long preparing = createOrder(otherCustomer, food);
        update(preparing, "CONFIRMED", 200);
        update(preparing, "PREPARING", 200);
        long last = createOrder(customer, food);
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId()).with(as(owner))
                        .param("status", "CREATED").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(last));
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId()).with(as(owner))
                        .param("status", "CREATED").param("size", "1").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(first));
        mvc.perform(get("/api/orders/restaurant/{id}", restaurant.getId()).with(as(owner))
                        .param("status", "PREPARING"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(preparing));
    }

    private void update(long id, String value, int expected) throws Exception {
        var result = mvc.perform(patch("/api/orders/{id}/status", id).with(as(owner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"" + value + "\"}"))
                .andExpect(status().is(expected));
        if (expected == 200) result.andExpect(jsonPath("$.status").value(value));
    }
}
