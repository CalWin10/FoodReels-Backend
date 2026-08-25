package com.foodreels.backend.restaurant;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.restaurant.RestaurantRequestDTO;
import com.foodreels.backend.restaurant.RestaurantResponseDTO;
import com.foodreels.backend.restaurant.Restaurant;
import com.foodreels.backend.exception.RestaurantNotFoundException;
import com.foodreels.backend.restaurant.RestaurantMapper;
import com.foodreels.backend.restaurant.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            RestaurantMapper restaurantMapper) {

        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    // Create restaurant
    public RestaurantResponseDTO createRestaurant(
            RestaurantRequestDTO requestDTO) {

        Restaurant restaurant = restaurantMapper.toEntity(requestDTO);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toResponseDTO(savedRestaurant);
    }

    // Get all restaurants
    public List<RestaurantResponseDTO> getAllRestaurants() {

        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();
    }

    // Get restaurant by id
    public RestaurantResponseDTO getRestaurantById(Long id) {

        Restaurant restaurant = findRestaurantById(id);

        return restaurantMapper.toResponseDTO(restaurant);
    }

    // Update restaurant
    public RestaurantResponseDTO updateRestaurant(
            Long id,
            RestaurantRequestDTO requestDTO) {

        Restaurant existingRestaurant = findRestaurantById(id);

        existingRestaurant.setName(requestDTO.getName());
        existingRestaurant.setDescription(requestDTO.getDescription());
        existingRestaurant.setAddress(requestDTO.getAddress());
        existingRestaurant.setPhoneNumber(requestDTO.getPhoneNumber());
        existingRestaurant.setImageUrl(requestDTO.getImageUrl());
        existingRestaurant.setWebsiteUrl(requestDTO.getWebsiteUrl());
        existingRestaurant.setLatitude(requestDTO.getLatitude());
        existingRestaurant.setLongitude(requestDTO.getLongitude());

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);

        return restaurantMapper.toResponseDTO(updatedRestaurant);
    }

    // Delete restaurant
    public void deleteRestaurant(Long id) {

        Restaurant restaurant = findRestaurantById(id);

        restaurantRepository.delete(restaurant);
    }

    // Internal helper
    private Restaurant findRestaurantById(Long id) {

        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with id: " + id));
    }
}

