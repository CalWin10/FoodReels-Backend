package com.foodreels.backend.dto;

public class SearchResultDTO<T> {

    private T data;

    private String type;

    private int relevanceScore;

    public SearchResultDTO() {
    }

    public SearchResultDTO(
            T data,
            String type,
            int relevanceScore) {

        this.data = data;
        this.type = type;
        this.relevanceScore = relevanceScore;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(
            int relevanceScore) {

        this.relevanceScore = relevanceScore;
    }
}