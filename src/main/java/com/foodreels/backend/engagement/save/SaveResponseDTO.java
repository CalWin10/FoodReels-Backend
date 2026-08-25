package com.foodreels.backend.engagement.save;

public class SaveResponseDTO {

    private boolean saved;
    private long savedCount;

    public SaveResponseDTO() {
    }

    public SaveResponseDTO(
            boolean saved,
            long savedCount) {

        this.saved = saved;
        this.savedCount = savedCount;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public long getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(long savedCount) {
        this.savedCount = savedCount;
    }
}

