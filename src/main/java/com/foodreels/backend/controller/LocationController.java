package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.NearbyDiscoveryResponseDTO;
import com.foodreels.backend.dto.NearbyFoodDTO;
import com.foodreels.backend.dto.NearbyReelDTO;
import com.foodreels.backend.dto.NearbyRestaurantDTO;
import com.foodreels.backend.service.LocationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/location")
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Location Discovery",
        description = "Nearby restaurants, foods and reels"
)
public class LocationController {

    private final LocationService
            locationService;

    public LocationController(
            LocationService locationService) {

        this.locationService =
                locationService;
    }

    // =========================================================
    // COMBINED NEARBY DISCOVERY
    // =========================================================

    @Operation(
            summary = "Get nearby discovery",
            description = "Returns nearby restaurants, foods and reels based on latitude, longitude and search radius.",
            parameters = {
                    @Parameter(name = "lat", description = "Latitude of the user's current location", example = "11.0168"),
                    @Parameter(name = "lng", description = "Longitude of the user's current location", example = "76.9558"),
                    @Parameter(name = "radiusKm", description = "Search radius in kilometers", example = "5")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/nearby")
    public ResponseEntity<
            NearbyDiscoveryResponseDTO>
            getNearbyDiscovery(

                    @RequestParam("lat")
                    double latitude,

                    @RequestParam("lng")
                    double longitude,

                    @RequestParam(
                            defaultValue = "5"
                    )
                    double radiusKm) {

        return ResponseEntity.ok(

                locationService
                        .getNearbyDiscovery(
                                latitude,
                                longitude,
                                radiusKm
                        )
        );
    }

    // =========================================================
    // NEARBY RESTAURANTS
    // =========================================================

    @Operation(
            summary = "Get nearby restaurants",
            description = "Returns restaurants within the search radius of the supplied latitude and longitude.",
            parameters = {
                    @Parameter(name = "lat", description = "Latitude of the user's current location", example = "11.0168"),
                    @Parameter(name = "lng", description = "Longitude of the user's current location", example = "76.9558"),
                    @Parameter(name = "radiusKm", description = "Search radius in kilometers", example = "5")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/restaurants/nearby")
    public ResponseEntity<
            List<NearbyRestaurantDTO>>
            getNearbyRestaurants(

                    @RequestParam("lat")
                    double latitude,

                    @RequestParam("lng")
                    double longitude,

                    @RequestParam(
                            defaultValue = "5"
                    )
                    double radiusKm) {

        return ResponseEntity.ok(

                locationService
                        .getNearbyRestaurants(
                                latitude,
                                longitude,
                                radiusKm
                        )
        );
    }

    // =========================================================
    // NEARBY FOODS
    // =========================================================

    @Operation(
            summary = "Get nearby food items",
            description = "Returns food items from restaurants within the search radius of the supplied latitude and longitude.",
            parameters = {
                    @Parameter(name = "lat", description = "Latitude of the user's current location", example = "11.0168"),
                    @Parameter(name = "lng", description = "Longitude of the user's current location", example = "76.9558"),
                    @Parameter(name = "radiusKm", description = "Search radius in kilometers", example = "5")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/foods/nearby")
    public ResponseEntity<
            List<NearbyFoodDTO>>
            getNearbyFoods(

                    @RequestParam("lat")
                    double latitude,

                    @RequestParam("lng")
                    double longitude,

                    @RequestParam(
                            defaultValue = "5"
                    )
                    double radiusKm) {

        return ResponseEntity.ok(

                locationService
                        .getNearbyFoods(
                                latitude,
                                longitude,
                                radiusKm
                        )
        );
    }

    // =========================================================
    // NEARBY REELS
    // =========================================================

    @Operation(
            summary = "Get nearby reels",
            description = "Returns reels associated with restaurants within the search radius of the supplied latitude and longitude.",
            parameters = {
                    @Parameter(name = "lat", description = "Latitude of the user's current location", example = "11.0168"),
                    @Parameter(name = "lng", description = "Longitude of the user's current location", example = "76.9558"),
                    @Parameter(name = "radiusKm", description = "Search radius in kilometers", example = "5")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/reels/nearby")
    public ResponseEntity<
            List<NearbyReelDTO>>
            getNearbyReels(

                    @RequestParam("lat")
                    double latitude,

                    @RequestParam("lng")
                    double longitude,

                    @RequestParam(
                            defaultValue = "5"
                    )
                    double radiusKm) {

        return ResponseEntity.ok(

                locationService
                        .getNearbyReels(
                                latitude,
                                longitude,
                                radiusKm
                        )
        );
    }
}