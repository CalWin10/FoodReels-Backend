package com.foodreels.backend.dto;

public class ReelScoreDTO {

    private Long reelId;

    private String caption;

    private String foodName;

    private String category;

    private int categoryScore;

    private int recencyScore;

    private int popularityScore;

    private int totalScore;

    private int watchPenalty;

    public int getWatchPenalty() {
        return watchPenalty;
    }

    public void setWatchPenalty(int watchPenalty) {
        this.watchPenalty = watchPenalty;
    }

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

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategoryScore() {
        return categoryScore;
    }

    public void setCategoryScore(int categoryScore) {
        this.categoryScore = categoryScore;
    }

    public int getRecencyScore() {
        return recencyScore;
    }

    public void setRecencyScore(int recencyScore) {
        this.recencyScore = recencyScore;
    }

    public int getPopularityScore() {
        return popularityScore;
    }

    public void setPopularityScore(int popularityScore) {
        this.popularityScore = popularityScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}