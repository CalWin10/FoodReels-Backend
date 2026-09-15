package com.foodreels.backend.dto;

import com.foodreels.backend.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequestDTO {

    @NotNull
    private OrderStatus status;

    public UpdateOrderStatusRequestDTO() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status) {

        this.status =
                status;
    }
}