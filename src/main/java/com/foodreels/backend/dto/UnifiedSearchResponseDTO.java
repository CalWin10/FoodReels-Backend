package com.foodreels.backend.dto;

import java.util.List;

public class UnifiedSearchResponseDTO {

    private String query;

    private List<FoodResponseDTO> foods;
    private long totalFoods;

    private List<RestaurantResponseDTO> restaurants;
    private long totalRestaurants;

    private List<ReelResponseDTO> reels;
    private long totalReels;

    public UnifiedSearchResponseDTO() {
    }

    public UnifiedSearchResponseDTO(
            String query,
            List<FoodResponseDTO> foods,
            long totalFoods,
            List<RestaurantResponseDTO> restaurants,
            long totalRestaurants,
            List<ReelResponseDTO> reels,
            long totalReels) {

        this.query = query;

        this.foods = foods;
        this.totalFoods = totalFoods;

        this.restaurants = restaurants;
        this.totalRestaurants = totalRestaurants;

        this.reels = reels;
        this.totalReels = totalReels;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(
            String query) {

        this.query = query;
    }

    public List<FoodResponseDTO> getFoods() {
        return foods;
    }

    public void setFoods(
            List<FoodResponseDTO> foods) {

        this.foods = foods;
    }

    public long getTotalFoods() {
        return totalFoods;
    }

    public void setTotalFoods(
            long totalFoods) {

        this.totalFoods = totalFoods;
    }

    public List<RestaurantResponseDTO> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(
            List<RestaurantResponseDTO> restaurants) {

        this.restaurants = restaurants;
    }

    public long getTotalRestaurants() {
        return totalRestaurants;
    }

    public void setTotalRestaurants(
            long totalRestaurants) {

        this.totalRestaurants = totalRestaurants;
    }

    public List<ReelResponseDTO> getReels() {
        return reels;
    }

    public void setReels(
            List<ReelResponseDTO> reels) {

        this.reels = reels;
    }

    public long getTotalReels() {
        return totalReels;
    }

    public void setTotalReels(
            long totalReels) {

        this.totalReels = totalReels;
    }
}