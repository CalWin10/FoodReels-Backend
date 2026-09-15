package com.foodreels.backend.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CreateOrderRequestDTO {

    @NotBlank
    private String deliveryAddress;

    @Valid
    @NotEmpty
    private List<OrderItemRequestDTO> items;

    public CreateOrderRequestDTO() {
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(
            String deliveryAddress) {

        this.deliveryAddress =
                deliveryAddress;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItemRequestDTO> items) {

        this.items = items;
    }
}