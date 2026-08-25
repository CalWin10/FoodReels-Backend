package com.foodreels.backend.reel;

import org.springframework.stereotype.Component;

import com.foodreels.backend.reel.ReelRequestDTO;
import com.foodreels.backend.reel.ReelResponseDTO;
import com.foodreels.backend.reel.Reel;

@Component
public class ReelMapper {

    public Reel toEntity(ReelRequestDTO dto) {

        Reel reel = new Reel();

        reel.setVideoUrl(dto.getVideoUrl());
        reel.setThumbnailUrl(dto.getThumbnailUrl());
        reel.setCaption(dto.getCaption());

        return reel;
    }

    public ReelResponseDTO toResponseDTO(Reel reel) {

        ReelResponseDTO dto = new ReelResponseDTO();

        dto.setId(reel.getId());
        dto.setVideoUrl(reel.getVideoUrl());
        dto.setThumbnailUrl(reel.getThumbnailUrl());
        dto.setCaption(reel.getCaption());
        dto.setViewCount(reel.getViewCount());

        dto.setFoodId(reel.getFood().getId());
        dto.setFoodName(reel.getFood().getName());

        dto.setCreatedAt(reel.getCreatedAt());
        dto.setUpdatedAt(reel.getUpdatedAt());

        return dto;
    }
}

