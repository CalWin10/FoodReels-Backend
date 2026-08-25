package com.foodreels.backend.food;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.food.Food;

public interface FoodRepository extends JpaRepository<Food,Long> {
    List<Food> findByRestaurantId(Long restaurantId);
}


