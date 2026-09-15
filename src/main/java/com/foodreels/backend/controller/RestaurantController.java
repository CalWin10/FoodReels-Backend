package com.foodreels.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Restaurants",
        description = "Restaurant management and discovery APIs"
)
public class RestaurantController {

    private final RestaurantService restaurantService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RestaurantController(
            RestaurantService restaurantService) {

        this.restaurantService =
                restaurantService;
    }


    // =========================================================
    // CREATE RESTAURANT
    // =========================================================

    @PostMapping
    public ResponseEntity<RestaurantResponseDTO>
            createRestaurant(

                    @AuthenticationPrincipal
                    Jwt jwt,

                    @Valid
                    @RequestBody
                    RestaurantRequestDTO requestDTO) {

        String email =
                jwt.getSubject();

        RestaurantResponseDTO response =
                restaurantService
                        .createRestaurant(
                                email,
                                requestDTO
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        response
                );
    }


    // =========================================================
    // GET ALL RESTAURANTS
    // =========================================================

    @GetMapping
    public ResponseEntity<
            List<RestaurantResponseDTO>>
            getAllRestaurants() {

        return ResponseEntity.ok(

                restaurantService
                        .getAllRestaurants()
        );
    }


    // =========================================================
    // GET RESTAURANT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO>
            getRestaurantById(

                    @PathVariable
                    Long id) {

        return ResponseEntity.ok(

                restaurantService
                        .getRestaurantById(
                                id
                        )
        );
    }


    // =========================================================
    // UPDATE RESTAURANT
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO>
            updateRestaurant(

                    @AuthenticationPrincipal
                    Jwt jwt,

                    @PathVariable
                    Long id,

                    @Valid
                    @RequestBody
                    RestaurantRequestDTO requestDTO) {

        String email =
                jwt.getSubject();

        boolean admin =
                isAdmin(
                        jwt
                );

        RestaurantResponseDTO response =
                restaurantService
                        .updateRestaurant(
                                email,
                                admin,
                                id,
                                requestDTO
                        );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // DELETE RESTAURANT
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
            deleteRestaurant(

                    @AuthenticationPrincipal
                    Jwt jwt,

                    @PathVariable
                    Long id) {

        String email =
                jwt.getSubject();

        boolean admin =
                isAdmin(
                        jwt
                );

        restaurantService
                .deleteRestaurant(
                        email,
                        admin,
                        id
                );

        return ResponseEntity
                .noContent()
                .build();
    }


    // =========================================================
    // CHECK ADMIN ROLE FROM JWT
    // =========================================================

    private boolean isAdmin(
            Jwt jwt) {

        String role =
                jwt.getClaimAsString(
                        "role"
                );

        if (role == null) {

            return false;
        }

        return role.equalsIgnoreCase(
                        "ADMIN"
                )
                ||
                role.equalsIgnoreCase(
                        "ROLE_ADMIN"
                );
    }
}