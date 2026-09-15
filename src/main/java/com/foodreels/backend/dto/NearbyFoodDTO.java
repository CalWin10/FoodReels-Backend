package com.foodreels.backend.dto;

public class NearbyFoodDTO {

    private FoodResponseDTO food;

    private double distanceKm;

    public NearbyFoodDTO() {
    }

    public NearbyFoodDTO(
            FoodResponseDTO food,
            double distanceKm) {

        this.food = food;
        this.distanceKm = distanceKm;
    }

    public FoodResponseDTO getFood() {
        return food;
    }

    public void setFood(
            FoodResponseDTO food) {

        this.food = food;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(
            double distanceKm) {

        this.distanceKm = distanceKm;
    }
}