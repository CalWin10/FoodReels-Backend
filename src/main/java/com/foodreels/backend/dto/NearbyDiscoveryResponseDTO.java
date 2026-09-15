package com.foodreels.backend.dto;

import java.util.List;

public class NearbyDiscoveryResponseDTO {

    private double latitude;

    private double longitude;

    private double radiusKm;

    private List<NearbyRestaurantDTO> restaurants;

    private List<NearbyFoodDTO> foods;

    private List<NearbyReelDTO> reels;

    public NearbyDiscoveryResponseDTO() {
    }

    public NearbyDiscoveryResponseDTO(
            double latitude,
            double longitude,
            double radiusKm,
            List<NearbyRestaurantDTO> restaurants,
            List<NearbyFoodDTO> foods,
            List<NearbyReelDTO> reels) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
        this.restaurants = restaurants;
        this.foods = foods;
        this.reels = reels;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(
            double latitude) {

        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(
            double longitude) {

        this.longitude = longitude;
    }

    public double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(
            double radiusKm) {

        this.radiusKm = radiusKm;
    }

    public List<NearbyRestaurantDTO>
            getRestaurants() {

        return restaurants;
    }

    public void setRestaurants(
            List<NearbyRestaurantDTO>
                    restaurants) {

        this.restaurants = restaurants;
    }

    public List<NearbyFoodDTO>
            getFoods() {

        return foods;
    }

    public void setFoods(
            List<NearbyFoodDTO> foods) {

        this.foods = foods;
    }

    public List<NearbyReelDTO>
            getReels() {

        return reels;
    }

    public void setReels(
            List<NearbyReelDTO> reels) {

        this.reels = reels;
    }
}