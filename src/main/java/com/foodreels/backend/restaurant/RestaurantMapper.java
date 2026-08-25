package com.foodreels.backend.restaurant;

import org.springframework.stereotype.Component;

import com.foodreels.backend.restaurant.RestaurantRequestDTO;
import com.foodreels.backend.restaurant.RestaurantResponseDTO;
import com.foodreels.backend.restaurant.Restaurant;

@Component
public class RestaurantMapper {
    public Restaurant toEntity(RestaurantRequestDTO dto) {
        Restaurant restaurant = new Restaurant();

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPhoneNumber(dto.getPhoneNumber());
        restaurant.setImageUrl(dto.getImageUrl());
        restaurant.setWebsiteUrl(dto.getWebsiteUrl());
        restaurant.setLatitude(dto.getLatitude());
        restaurant.setLongitude(dto.getLongitude());
        restaurant.setRating(0.0);

        return restaurant;
    }

    public RestaurantResponseDTO toResponseDTO(Restaurant restaurant) {
        RestaurantResponseDTO dto = new RestaurantResponseDTO();

        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setAddress(restaurant.getAddress());
        dto.setPhoneNumber(restaurant.getPhoneNumber());
        dto.setImageUrl(restaurant.getImageUrl());
        dto.setWebsiteUrl(restaurant.getWebsiteUrl());
        dto.setLatitude(restaurant.getLatitude());
        dto.setLongitude(restaurant.getLongitude());
        dto.setRating(restaurant.getRating());
        dto.setCreatedAt(restaurant.getCreatedAt());
        //dto.setUpdatedAt(restaurant.getUpdatedAt());

        return dto;
    }
}


