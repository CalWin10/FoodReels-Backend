package com.foodreels.backend.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.restaurant.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
}


