package com.foodreels.backend.reel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReelRequestDTO {

    @NotBlank(message = "Video URL is required")
    @Pattern(
        regexp = "^https?://.+",
        message = "Video URL must be a valid HTTP or HTTPS URL"
    )
    private String videoUrl;

    @Pattern(
        regexp = "^$|^https?://.+",
        message = "Thumbnail URL must be a valid HTTP or HTTPS URL"
    )
    private String thumbnailUrl;

    @Size(
        max = 500,
        message = "Caption cannot exceed 500 characters"
    )
    private String caption;

    @NotNull(message = "Food ID is required")
    private Long foodId;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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
}

