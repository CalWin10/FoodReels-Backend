package com.foodreels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
}
