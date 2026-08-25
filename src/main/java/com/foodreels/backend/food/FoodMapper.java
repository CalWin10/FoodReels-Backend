package com.foodreels.backend.food;

import org.springframework.stereotype.Component;

import com.foodreels.backend.food.FoodRequestDTO;
import com.foodreels.backend.food.FoodResponseDTO;
import com.foodreels.backend.food.Food;

@Component
public class FoodMapper {

    public Food toEntity(FoodRequestDTO dto) {

        Food food = new Food();

        food.setName(dto.getName());
        food.setDescription(dto.getDescription());
        food.setPrice(dto.getPrice());
        food.setImageUrl(dto.getImageUrl());
        food.setCategory(dto.getCategory());

        return food;
    }

    public FoodResponseDTO toResponseDTO(Food food) {

        FoodResponseDTO dto = new FoodResponseDTO();

        dto.setId(food.getId());
        dto.setName(food.getName());
        dto.setDescription(food.getDescription());
        dto.setPrice(food.getPrice());
        dto.setImageUrl(food.getImageUrl());
        dto.setCategory(food.getCategory());

        dto.setRestaurantId(food.getRestaurant().getId());
        dto.setRestaurantName(food.getRestaurant().getName());

        dto.setCreatedAt(food.getCreatedAt());
        dto.setUpdatedAt(food.getUpdatedAt());

        return dto;
    }
}

