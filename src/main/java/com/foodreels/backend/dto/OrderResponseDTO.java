package com.foodreels.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.foodreels.backend.entity.OrderStatus;

public class OrderResponseDTO {

    private Long id;

    private Long userId;

    private Long restaurantId;

    private String restaurantName;

    private List<OrderItemResponseDTO> items;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private String deliveryAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(
            Long id,
            Long userId,
            Long restaurantId,
            String restaurantName,
            List<OrderItemResponseDTO> items,
            BigDecimal totalAmount,
            OrderStatus status,
            String deliveryAddress,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(
            Long userId) {

        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(
            Long restaurantId) {

        this.restaurantId =
                restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(
            String restaurantName) {

        this.restaurantName =
                restaurantName;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItemResponseDTO> items) {

        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status) {

        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(
            String deliveryAddress) {

        this.deliveryAddress =
                deliveryAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt =
                updatedAt;
    }
}