package com.foodreels.backend.dto;

public class NearbyReelDTO {

    private ReelResponseDTO reel;

    private double distanceKm;

    public NearbyReelDTO() {
    }

    public NearbyReelDTO(
            ReelResponseDTO reel,
            double distanceKm) {

        this.reel = reel;
        this.distanceKm = distanceKm;
    }

    public ReelResponseDTO getReel() {
        return reel;
    }

    public void setReel(
            ReelResponseDTO reel) {

        this.reel = reel;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(
            double distanceKm) {

        this.distanceKm = distanceKm;
    }
}