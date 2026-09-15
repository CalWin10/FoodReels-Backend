package com.foodreels.backend.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.foodreels.backend.dto.RestaurantRequestDTO;
import com.foodreels.backend.dto.RestaurantResponseDTO;
import com.foodreels.backend.entity.Restaurant;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.exception.RestaurantNotFoundException;
import com.foodreels.backend.mapper.RestaurantMapper;
import com.foodreels.backend.repository.RestaurantRepository;
import com.foodreels.backend.repository.UserRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    private final UserRepository userRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            RestaurantMapper restaurantMapper,
            UserRepository userRepository) {

        this.restaurantRepository =
                restaurantRepository;

        this.restaurantMapper =
                restaurantMapper;

        this.userRepository =
                userRepository;
    }


    // =========================================================
    // CREATE RESTAURANT
    // =========================================================

    @Transactional
    @Caching(
            evict = {

                @CacheEvict(
                        value = "searchResults",
                        allEntries = true
                ),

                @CacheEvict(
                        value = "nearbyDiscovery",
                        allEntries = true
                )
            }
    )
    public RestaurantResponseDTO createRestaurant(
            String email,
            RestaurantRequestDTO requestDTO) {

        // -----------------------------------------------------
        // FIND AUTHENTICATED USER
        // -----------------------------------------------------

        User owner =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "User not found"
                                    )
                        );


        // -----------------------------------------------------
        // MAP DTO TO ENTITY
        // -----------------------------------------------------

        Restaurant restaurant =
                restaurantMapper
                        .toEntity(
                                requestDTO
                        );


        // -----------------------------------------------------
        // ASSIGN OWNER FROM JWT USER
        // -----------------------------------------------------
        //
        // The frontend/client does NOT send ownerId.
        //
        // JWT email
        //      ↓
        // User
        //      ↓
        // Restaurant.owner
        //
        // -----------------------------------------------------

        restaurant.setOwner(
                owner
        );


        // -----------------------------------------------------
        // SAVE RESTAURANT
        // -----------------------------------------------------

        Restaurant savedRestaurant =
                restaurantRepository
                        .save(
                                restaurant
                        );


        return restaurantMapper
                .toResponseDTO(
                        savedRestaurant
                );
    }


    // =========================================================
    // GET ALL RESTAURANTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO>
            getAllRestaurants() {

        return restaurantRepository
                .findAll()
                .stream()
                .map(
                        restaurantMapper
                                ::toResponseDTO
                )
                .toList();
    }


    // =========================================================
    // GET RESTAURANT BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public RestaurantResponseDTO
            getRestaurantById(
                    Long id) {

        Restaurant restaurant =
                findRestaurantById(
                        id
                );

        return restaurantMapper
                .toResponseDTO(
                        restaurant
                );
    }


    // =========================================================
    // UPDATE RESTAURANT
    // =========================================================

    @Transactional
    @Caching(
            evict = {

                @CacheEvict(
                        value = "searchResults",
                        allEntries = true
                ),

                @CacheEvict(
                        value = "nearbyDiscovery",
                        allEntries = true
                )
            }
    )
    public RestaurantResponseDTO
            updateRestaurant(

                    String email,
                    boolean admin,
                    Long id,
                    RestaurantRequestDTO requestDTO) {

        // -----------------------------------------------------
        // FIND RESTAURANT
        // -----------------------------------------------------

        Restaurant existingRestaurant =
                findRestaurantById(
                        id
                );


        // -----------------------------------------------------
        // OWNERSHIP CHECK
        // -----------------------------------------------------

        validateRestaurantOwnership(
                existingRestaurant,
                email,
                admin
        );


        // -----------------------------------------------------
        // UPDATE FIELDS
        // -----------------------------------------------------

        existingRestaurant.setName(
                requestDTO.getName()
        );

        existingRestaurant.setDescription(
                requestDTO.getDescription()
        );

        existingRestaurant.setAddress(
                requestDTO.getAddress()
        );

        existingRestaurant.setPhoneNumber(
                requestDTO.getPhoneNumber()
        );

        existingRestaurant.setImageUrl(
                requestDTO.getImageUrl()
        );

        existingRestaurant.setWebsiteUrl(
                requestDTO.getWebsiteUrl()
        );

        existingRestaurant.setLatitude(
                requestDTO.getLatitude()
        );

        existingRestaurant.setLongitude(
                requestDTO.getLongitude()
        );


        // -----------------------------------------------------
        // IMPORTANT
        // -----------------------------------------------------
        //
        // We DO NOT change:
        //
        // existingRestaurant.setOwner(...)
        //
        // during update.
        //
        // Otherwise a normal update could accidentally
        // transfer restaurant ownership.
        //
        // -----------------------------------------------------


        Restaurant updatedRestaurant =
                restaurantRepository
                        .save(
                                existingRestaurant
                        );


        return restaurantMapper
                .toResponseDTO(
                        updatedRestaurant
                );
    }


    // =========================================================
    // DELETE RESTAURANT
    // =========================================================

    @Transactional
    @Caching(
            evict = {

                @CacheEvict(
                        value = "searchResults",
                        allEntries = true
                ),

                @CacheEvict(
                        value = "nearbyDiscovery",
                        allEntries = true
                )
            }
    )
    public void deleteRestaurant(

            String email,
            boolean admin,
            Long id) {

        Restaurant restaurant =
                findRestaurantById(
                        id
                );


        // -----------------------------------------------------
        // OWNERSHIP CHECK
        // -----------------------------------------------------

        validateRestaurantOwnership(
                restaurant,
                email,
                admin
        );


        restaurantRepository
                .delete(
                        restaurant
                );
    }


    // =========================================================
    // CHECK RESTAURANT OWNERSHIP
    // =========================================================

    private void validateRestaurantOwnership(

            Restaurant restaurant,
            String email,
            boolean admin) {


        // -----------------------------------------------------
        // ADMIN CAN MANAGE ANY RESTAURANT
        // -----------------------------------------------------

        if (admin) {

            return;
        }


        // -----------------------------------------------------
        // RESTAURANT MUST HAVE AN OWNER
        // -----------------------------------------------------

        if (restaurant.getOwner() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This restaurant does not have an assigned owner"
            );
        }


        // -----------------------------------------------------
        // CURRENT USER MUST OWN RESTAURANT
        // -----------------------------------------------------

        String ownerEmail =
                restaurant
                        .getOwner()
                        .getEmail();


        if (ownerEmail == null
                ||
                !ownerEmail
                        .equalsIgnoreCase(
                                email
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not own this restaurant"
            );
        }
    }


    // =========================================================
    // FIND RESTAURANT
    // =========================================================

    private Restaurant findRestaurantById(
            Long id) {

        return restaurantRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                            new RestaurantNotFoundException(
                                    "Restaurant not found with id: "
                                            + id
                            )
                );
    }
}