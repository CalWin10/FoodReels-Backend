package com.foodreels.backend.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.foodreels.backend.dto.NearbyDiscoveryResponseDTO;
import com.foodreels.backend.dto.NearbyFoodDTO;
import com.foodreels.backend.dto.NearbyReelDTO;
import com.foodreels.backend.dto.NearbyRestaurantDTO;
import com.foodreels.backend.entity.Food;
import com.foodreels.backend.entity.Reel;
import com.foodreels.backend.entity.Restaurant;
import com.foodreels.backend.mapper.FoodMapper;
import com.foodreels.backend.mapper.ReelMapper;
import com.foodreels.backend.mapper.RestaurantMapper;
import com.foodreels.backend.repository.FoodRepository;
import com.foodreels.backend.repository.ReelRepository;
import com.foodreels.backend.repository.RestaurantRepository;

@Service
public class LocationService {

    private static final double EARTH_RADIUS_KM =
            6371.0;

    private static final double MAX_RADIUS_KM =
            100.0;

    private static final double KM_PER_LATITUDE_DEGREE =
            111.32;

    private final RestaurantRepository
            restaurantRepository;

    private final FoodRepository
            foodRepository;

    private final ReelRepository
            reelRepository;

    private final RestaurantMapper
            restaurantMapper;

    private final FoodMapper
            foodMapper;

    private final ReelMapper
            reelMapper;

    public LocationService(
            RestaurantRepository restaurantRepository,
            FoodRepository foodRepository,
            ReelRepository reelRepository,
            RestaurantMapper restaurantMapper,
            FoodMapper foodMapper,
            ReelMapper reelMapper) {

        this.restaurantRepository =
                restaurantRepository;

        this.foodRepository =
                foodRepository;

        this.reelRepository =
                reelRepository;

        this.restaurantMapper =
                restaurantMapper;

        this.foodMapper =
                foodMapper;

        this.reelMapper =
                reelMapper;
    }

    // =========================================================
    // COMBINED NEARBY DISCOVERY
    // =========================================================

    @Cacheable(
            value = "nearbyDiscovery",
            key =
                "T(java.lang.Math).round(#p0 * 1000)"
                + " + ':' + "
                + "T(java.lang.Math).round(#p1 * 1000)"
                + " + ':radius:' + "
                + "T(java.lang.Math).round(#p2 * 10)"
    )
    public NearbyDiscoveryResponseDTO
            getNearbyDiscovery(

                    double userLatitude,
                    double userLongitude,
                    double radiusKm) {

        System.out.println(
                "LOCATION DISCOVERY DATABASE EXECUTED - CACHE MISS"
        );

        validateLocationRequest(
                userLatitude,
                userLongitude,
                radiusKm
        );

        List<NearbyRestaurantDTO> restaurants =
                getNearbyRestaurants(
                        userLatitude,
                        userLongitude,
                        radiusKm
                );

        List<NearbyFoodDTO> foods =
                getNearbyFoods(
                        userLatitude,
                        userLongitude,
                        radiusKm
                );

        List<NearbyReelDTO> reels =
                getNearbyReels(
                        userLatitude,
                        userLongitude,
                        radiusKm
                );

        return new NearbyDiscoveryResponseDTO(
                userLatitude,
                userLongitude,
                radiusKm,
                restaurants,
                foods,
                reels
        );
    }

    // =========================================================
    // NEARBY RESTAURANTS
    // =========================================================

    public List<NearbyRestaurantDTO>
            getNearbyRestaurants(

                    double userLatitude,
                    double userLongitude,
                    double radiusKm) {

        validateLocationRequest(
                userLatitude,
                userLongitude,
                radiusKm
        );

        BoundingBox box =
                calculateBoundingBox(
                        userLatitude,
                        userLongitude,
                        radiusKm
                );

        List<Restaurant> candidates =
                restaurantRepository
                        .findNearbyCandidates(
                                box.minLat(),
                                box.maxLat(),
                                box.minLng(),
                                box.maxLng()
                        );

        return candidates
                .stream()

                .map(
                        restaurant -> {

                            double distance =
                                    calculateDistance(
                                            userLatitude,
                                            userLongitude,
                                            restaurant
                                                    .getLatitude(),
                                            restaurant
                                                    .getLongitude()
                                    );

                            return new NearbyRestaurantDTO(
                                    restaurantMapper
                                            .toResponseDTO(
                                                restaurant
                                            ),

                                    roundDistance(
                                            distance
                                    )
                            );
                        }
                )

                .filter(
                        result ->
                                result.getDistanceKm()
                                        <= radiusKm
                )

                .sorted(
                        Comparator
                                .comparingDouble(
                                        NearbyRestaurantDTO
                                                ::getDistanceKm
                                )
                )

                .toList();
    }

    // =========================================================
    // NEARBY FOODS
    // =========================================================

    public List<NearbyFoodDTO>
            getNearbyFoods(

                    double userLatitude,
                    double userLongitude,
                    double radiusKm) {

        validateLocationRequest(
                userLatitude,
                userLongitude,
                radiusKm
        );

        BoundingBox box =
                calculateBoundingBox(
                        userLatitude,
                        userLongitude,
                        radiusKm
                );

        List<Food> candidates =
                foodRepository
                        .findNearbyCandidates(
                                box.minLat(),
                                box.maxLat(),
                                box.minLng(),
                                box.maxLng()
                        );

        return candidates
                .stream()

                .map(
                        food -> {

                            Restaurant restaurant =
                                    food.getRestaurant();

                            double distance =
                                    calculateDistance(
                                            userLatitude,
                                            userLongitude,
                                            restaurant
                                                    .getLatitude(),
                                            restaurant
                                                    .getLongitude()
                                    );

                            return new NearbyFoodDTO(
                                    foodMapper
                                            .toResponseDTO(
                                                food
                                            ),

                                    roundDistance(
                                            distance
                                    )
                            );
                        }
                )

                .filter(
                        result ->
                                result.getDistanceKm()
                                        <= radiusKm
                )

                .sorted(
                        Comparator
                                .comparingDouble(
                                        NearbyFoodDTO
                                                ::getDistanceKm
                                )
                )

                .toList();
    }

    // =========================================================
    // NEARBY REELS
    // =========================================================

    public List<NearbyReelDTO>
            getNearbyReels(

                    double userLatitude,
                    double userLongitude,
                    double radiusKm) {

        validateLocationRequest(
                userLatitude,
                userLongitude,
                radiusKm
        );

        BoundingBox box =
                calculateBoundingBox(
                        userLatitude,
                        userLongitude,
                        radiusKm
                );

        List<Reel> candidates =
                reelRepository
                        .findNearbyCandidates(
                                box.minLat(),
                                box.maxLat(),
                                box.minLng(),
                                box.maxLng()
                        );

        return candidates
                .stream()

                .map(
                        reel -> {

                            Restaurant restaurant =
                                    reel.getFood()
                                            .getRestaurant();

                            double distance =
                                    calculateDistance(
                                            userLatitude,
                                            userLongitude,
                                            restaurant
                                                    .getLatitude(),
                                            restaurant
                                                    .getLongitude()
                                    );

                            return new NearbyReelDTO(
                                    reelMapper
                                            .toResponseDTO(
                                                reel
                                            ),

                                    roundDistance(
                                            distance
                                    )
                            );
                        }
                )

                .filter(
                        result ->
                                result.getDistanceKm()
                                        <= radiusKm
                )

                .sorted(
                        Comparator
                                .comparingDouble(
                                        NearbyReelDTO
                                                ::getDistanceKm
                                )
                )

                .toList();
    }

    // =========================================================
    // BOUNDING BOX
    // =========================================================

    private BoundingBox calculateBoundingBox(
            double latitude,
            double longitude,
            double radiusKm) {

        double latitudeDelta =
                radiusKm
                        /
                KM_PER_LATITUDE_DEGREE;

        double cosineLatitude =
                Math.cos(
                        Math.toRadians(
                                latitude
                        )
                );

        double longitudeDelta;

        if (Math.abs(cosineLatitude) < 0.01) {

            longitudeDelta =
                    180.0;

        } else {

            longitudeDelta =
                    radiusKm
                            /
                    (
                        KM_PER_LATITUDE_DEGREE
                        *
                        Math.abs(
                                cosineLatitude
                        )
                    );
        }

        double minLat =
                Math.max(
                        -90.0,
                        latitude
                                -
                        latitudeDelta
                );

        double maxLat =
                Math.min(
                        90.0,
                        latitude
                                +
                        latitudeDelta
                );

        double minLng =
                Math.max(
                        -180.0,
                        longitude
                                -
                        longitudeDelta
                );

        double maxLng =
                Math.min(
                        180.0,
                        longitude
                                +
                        longitudeDelta
                );

        return new BoundingBox(
                minLat,
                maxLat,
                minLng,
                maxLng
        );
    }

    // =========================================================
    // HAVERSINE DISTANCE
    // =========================================================

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        double latitudeDistance =
                Math.toRadians(
                        lat2 - lat1
                );

        double longitudeDistance =
                Math.toRadians(
                        lon2 - lon1
                );

        double a =
                Math.sin(
                        latitudeDistance / 2
                )
                *
                Math.sin(
                        latitudeDistance / 2
                )

                +

                Math.cos(
                        Math.toRadians(
                                lat1
                        )
                )
                *
                Math.cos(
                        Math.toRadians(
                                lat2
                        )
                )
                *
                Math.sin(
                        longitudeDistance / 2
                )
                *
                Math.sin(
                        longitudeDistance / 2
                );

        double c =
                2
                *
                Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(
                                1 - a
                        )
                );

        return EARTH_RADIUS_KM * c;
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateLocationRequest(
            double latitude,
            double longitude,
            double radiusKm) {

        if (latitude < -90
                ||
                latitude > 90) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Latitude must be between -90 and 90"
            );
        }

        if (longitude < -180
                ||
                longitude > 180) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Longitude must be between -180 and 180"
            );
        }

        if (radiusKm <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Radius must be greater than 0 km"
            );
        }

        if (radiusKm > MAX_RADIUS_KM) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Radius cannot exceed 100 km"
            );
        }
    }

    // =========================================================
    // ROUND DISTANCE
    // =========================================================

    private double roundDistance(
            double distance) {

        return Math.round(
                distance * 100.0
        ) / 100.0;
    }

    // =========================================================
    // INTERNAL BOUNDING BOX MODEL
    // =========================================================

    private record BoundingBox(
            double minLat,
            double maxLat,
            double minLng,
            double maxLng) {
    }
}