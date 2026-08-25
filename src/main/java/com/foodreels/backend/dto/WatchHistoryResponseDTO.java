package com.foodreels.backend.dto;

import java.time.LocalDateTime;

public class WatchHistoryResponseDTO {

    private Long reelId;
    private String caption;

    private Long foodId;
    private String foodName;

    private Integer watchCount;

    private LocalDateTime firstWatchedAt;
    private LocalDateTime lastWatchedAt;

    public Long getReelId() {
        return reelId;
    }

    public void setReelId(Long reelId) {
        this.reelId = reelId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    public LocalDateTime getFirstWatchedAt() {
        return firstWatchedAt;
    }

    public void setFirstWatchedAt(LocalDateTime firstWatchedAt) {
        this.firstWatchedAt = firstWatchedAt;
    }

    public LocalDateTime getLastWatchedAt() {
        return lastWatchedAt;
    }

    public void setLastWatchedAt(LocalDateTime lastWatchedAt) {
        this.lastWatchedAt = lastWatchedAt;
    }
}