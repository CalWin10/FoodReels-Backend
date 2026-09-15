package com.foodreels.backend.controller;

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