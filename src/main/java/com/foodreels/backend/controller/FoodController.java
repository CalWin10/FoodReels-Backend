package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/foods")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Foods", description = "Food item management and restaurant menu APIs.")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    // Create food
    @Operation(
            summary = "Create a food item",
            description = "Creates a food item linked to a restaurant. Requires the RESTAURANT_OWNER or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
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
    @Operation(
            summary = "Get all food items",
            description = "Returns all food items available in FoodReels."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<List<FoodResponseDTO>> getAllFoods() {

        List<FoodResponseDTO> foods =
                foodService.getAllFoods();

        return ResponseEntity.ok(foods);
    }

    // Get food by ID
    @Operation(
            summary = "Get a food item",
            description = "Returns the food item identified by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "Food ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> getFoodById(
            @PathVariable Long id) {

        FoodResponseDTO food =
                foodService.getFoodById(id);

        return ResponseEntity.ok(food);
    }

    // Get foods belonging to a restaurant
    @Operation(
            summary = "Get a restaurant menu",
            description = "Returns food items belonging to the specified restaurant.",
            parameters = {
                    @Parameter(name = "restaurantId", description = "Restaurant ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodResponseDTO>> getFoodsByRestaurant(
            @PathVariable Long restaurantId) {

        List<FoodResponseDTO> foods =
                foodService.getFoodsByRestaurantId(restaurantId);

        return ResponseEntity.ok(foods);
    }

    // Update food
    @Operation(
            summary = "Update a food item",
            description = "Updates the specified food item using the supplied details. Requires the RESTAURANT_OWNER or ADMIN role.",
            parameters = {
                    @Parameter(name = "id", description = "Food ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody FoodRequestDTO requestDTO) {

        FoodResponseDTO updatedFood =
                foodService.updateFood(id, requestDTO);

        return ResponseEntity.ok(updatedFood);
    }

    // Delete food
    @Operation(
            summary = "Delete a food item",
            description = "Deletes the specified food item. Requires the RESTAURANT_OWNER or ADMIN role.",
            parameters = {
                    @Parameter(name = "id", description = "Food ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(
            @PathVariable Long id) {

        foodService.deleteFood(id);

        return ResponseEntity.noContent().build();
    }
}