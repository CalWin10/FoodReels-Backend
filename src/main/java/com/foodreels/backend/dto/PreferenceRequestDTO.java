package com.foodreels.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PreferenceRequestDTO {

    @NotBlank(
        message = "Category is required"
    )
    @Size(
        max = 100,
        message = "Category cannot exceed 100 characters"
    )
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}