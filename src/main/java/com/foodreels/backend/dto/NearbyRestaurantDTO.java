package com.foodreels.backend.dto;

public class NearbyRestaurantDTO {

    private RestaurantResponseDTO restaurant;

    private double distanceKm;

    public NearbyRestaurantDTO() {
    }

    public NearbyRestaurantDTO(
            RestaurantResponseDTO restaurant,
            double distanceKm) {

        this.restaurant = restaurant;
        this.distanceKm = distanceKm;
    }

    public RestaurantResponseDTO getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(
            RestaurantResponseDTO restaurant) {

        this.restaurant = restaurant;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(
            double distanceKm) {

        this.distanceKm = distanceKm;
    }
}