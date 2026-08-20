package com.foodreels.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.Food;

public interface FoodRepository extends JpaRepository<Food,Long> {
    List<Food> findByRestaurantId(Long restaurantId);
}
