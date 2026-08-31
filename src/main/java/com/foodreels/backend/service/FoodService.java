package com.foodreels.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.FoodRequestDTO;
import com.foodreels.backend.dto.FoodResponseDTO;
import com.foodreels.backend.entity.Food;
import com.foodreels.backend.entity.Restaurant;
import com.foodreels.backend.exception.FoodNotFoundException;
import com.foodreels.backend.exception.RestaurantNotFoundException;
import com.foodreels.backend.mapper.FoodMapper;
import com.foodreels.backend.repository.FoodRepository;
import com.foodreels.backend.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodMapper foodMapper;

    public FoodService(
            FoodRepository foodRepository,
            RestaurantRepository restaurantRepository,
            FoodMapper foodMapper) {

        this.foodRepository = foodRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodMapper = foodMapper;
    }

    // Create food
    @CacheEvict(value = "searchResults", allEntries = true)
    public FoodResponseDTO createFood(FoodRequestDTO requestDTO) {

        Food food = foodMapper.toEntity(requestDTO);

        Restaurant restaurant = findRestaurantById(
                requestDTO.getRestaurantId());

        food.setRestaurant(restaurant);

        Food savedFood = foodRepository.save(food);

        return foodMapper.toResponseDTO(savedFood);
    }

    // Get all foods
    public List<FoodResponseDTO> getAllFoods() {

        return foodRepository.findAll()
                .stream()
                .map(foodMapper::toResponseDTO)
                .toList();
    }

    // Get food by ID
    public FoodResponseDTO getFoodById(Long id) {

        Food food = findFoodById(id);

        return foodMapper.toResponseDTO(food);
    }

    // Get foods belonging to one restaurant
    public List<FoodResponseDTO> getFoodsByRestaurantId(Long restaurantId) {

        // Make sure restaurant actually exists
        findRestaurantById(restaurantId);

        return foodRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(foodMapper::toResponseDTO)
                .toList();
    }

    // Update food
    @CacheEvict(value = "searchResults", allEntries = true)
    public FoodResponseDTO updateFood(
            Long id,
            FoodRequestDTO requestDTO) {

        Food existingFood = findFoodById(id);

        Restaurant restaurant = findRestaurantById(
                requestDTO.getRestaurantId());

        existingFood.setName(requestDTO.getName());
        existingFood.setDescription(requestDTO.getDescription());
        existingFood.setPrice(requestDTO.getPrice());
        existingFood.setImageUrl(requestDTO.getImageUrl());
        existingFood.setCategory(requestDTO.getCategory());

        existingFood.setRestaurant(restaurant);

        Food updatedFood = foodRepository.save(existingFood);

        return foodMapper.toResponseDTO(updatedFood);
    }

    // Delete food
    @CacheEvict(value = "searchResults", allEntries = true)
    public void deleteFood(Long id) {

        Food food = findFoodById(id);

        foodRepository.delete(food);
    }

    // Internal helper - find food entity
    private Food findFoodById(Long id) {

        return foodRepository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException(
                        "Food not found with id: " + id));
    }

    // Internal helper - find restaurant entity
    private Restaurant findRestaurantById(Long id) {

        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with id: " + id));
    }
}