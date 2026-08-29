package com.foodreels.backend.dto;

public class CategoryScoreDTO {

    private String category;
    private int score;

    public CategoryScoreDTO() {
    }

    public CategoryScoreDTO(
            String category,
            int score) {

        this.category = category;
        this.score = score;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}