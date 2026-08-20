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

import com.foodreels.backend.dto.RestaurantRequestDTO;
import com.foodreels.backend.dto.RestaurantResponseDTO;
import com.foodreels.backend.service.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // Create restaurant
    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(
            @Valid @RequestBody RestaurantRequestDTO requestDTO) {

        RestaurantResponseDTO createdRestaurant =
                restaurantService.createRestaurant(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRestaurant);
    }

    // Get all restaurants
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {

        List<RestaurantResponseDTO> restaurants =
                restaurantService.getAllRestaurants();

        return ResponseEntity.ok(restaurants);
    }

    // Get restaurant by ID
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(
            @PathVariable Long id) {

        RestaurantResponseDTO restaurant =
                restaurantService.getRestaurantById(id);

        return ResponseEntity.ok(restaurant);
    }

    // Update restaurant
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDTO requestDTO) {

        RestaurantResponseDTO updatedRestaurant =
                restaurantService.updateRestaurant(id, requestDTO);

        return ResponseEntity.ok(updatedRestaurant);
    }

    // Delete restaurant
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long id) {

        restaurantService.deleteRestaurant(id);

        return ResponseEntity.noContent().build();
    }
}