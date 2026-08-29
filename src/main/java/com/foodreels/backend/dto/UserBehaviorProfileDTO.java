package com.foodreels.backend.dto;

import java.util.Map;
import java.util.Set;

public class UserBehaviorProfileDTO {

    private Long userId;

    private Set<String> preferences;

    private Map<String, Integer> watchCategoryCounts;

    private Map<String, Integer> likeCategoryCounts;

    private Map<String, Integer> saveCategoryCounts;

    private Map<String, Integer> commentCategoryCounts;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<String> preferences) {
        this.preferences = preferences;
    }

    public Map<String, Integer> getWatchCategoryCounts() {
        return watchCategoryCounts;
    }

    public void setWatchCategoryCounts(
            Map<String, Integer> watchCategoryCounts) {

        this.watchCategoryCounts = watchCategoryCounts;
    }

    public Map<String, Integer> getLikeCategoryCounts() {
        return likeCategoryCounts;
    }

    public void setLikeCategoryCounts(
            Map<String, Integer> likeCategoryCounts) {

        this.likeCategoryCounts = likeCategoryCounts;
    }

    public Map<String, Integer> getSaveCategoryCounts() {
        return saveCategoryCounts;
    }

    public void setSaveCategoryCounts(
            Map<String, Integer> saveCategoryCounts) {

        this.saveCategoryCounts = saveCategoryCounts;
    }

    public Map<String, Integer> getCommentCategoryCounts() {
        return commentCategoryCounts;
    }

    public void setCommentCategoryCounts(
            Map<String, Integer> commentCategoryCounts) {

        this.commentCategoryCounts = commentCategoryCounts;
    }
}