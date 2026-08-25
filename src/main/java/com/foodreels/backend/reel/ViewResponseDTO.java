package com.foodreels.backend.reel;

public class ViewResponseDTO {

    private Long reelId;
    private Long viewCount;

    public ViewResponseDTO() {
    }

    public ViewResponseDTO(Long reelId, Long viewCount) {
        this.reelId = reelId;
        this.viewCount = viewCount;
    }

    public Long getReelId() {
        return reelId;
    }

    public void setReelId(Long reelId) {
        this.reelId = reelId;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
}

