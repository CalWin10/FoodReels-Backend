package com.foodreels.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.FoodRequestDTO;
import com.foodreels.backend.dto.FoodResponseDTO;
import com.foodreels.backend.service.FoodService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    // Create food
    @PostMapping
    public ResponseEntity<FoodResponseDTO> createFood(
            @Valid @RequestBody FoodRequestDTO requestDTO) {

        FoodResponseDTO createdFood =
                foodService.createFood(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdFood);
    }

    // Get all foods
    @GetMapping
    public ResponseEntity<List<FoodResponseDTO>> getAllFoods() {

        List<FoodResponseDTO> foods =
                foodService.getAllFoods();

        return ResponseEntity.ok(foods);
    }

    // Get food by ID
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> getFoodById(
            @PathVariable Long id) {

        FoodResponseDTO food =
                foodService.getFoodById(id);

        return ResponseEntity.ok(food);
    }

    // Get foods belonging to a restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodResponseDTO>> getFoodsByRestaurant(
            @PathVariable Long restaurantId) {

        List<FoodResponseDTO> foods =
                foodService.getFoodsByRestaurantId(restaurantId);

        return ResponseEntity.ok(foods);
    }

    // Update food
    @PutMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody FoodRequestDTO requestDTO) {

        FoodResponseDTO updatedFood =
                foodService.updateFood(id, requestDTO);

        return ResponseEntity.ok(updatedFood);
    }

    // Delete food
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(
            @PathVariable Long id) {

        foodService.deleteFood(id);

        return ResponseEntity.noContent().build();
    }
}