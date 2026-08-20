package com.foodreels.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.ReelRequestDTO;
import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.entity.Food;
import com.foodreels.backend.entity.Reel;
import com.foodreels.backend.exception.FoodNotFoundException;
import com.foodreels.backend.exception.ReelNotFoundException;
import com.foodreels.backend.mapper.ReelMapper;
import com.foodreels.backend.repository.FoodRepository;
import com.foodreels.backend.repository.ReelRepository;

@Service
public class ReelService {

    private final ReelRepository reelRepository;
    private final FoodRepository foodRepository;
    private final ReelMapper reelMapper;

    public ReelService(
            ReelRepository reelRepository,
            FoodRepository foodRepository,
            ReelMapper reelMapper) {

        this.reelRepository = reelRepository;
        this.foodRepository = foodRepository;
        this.reelMapper = reelMapper;
    }

    // Create reel
    public ReelResponseDTO createReel(ReelRequestDTO requestDTO) {

        Reel reel = reelMapper.toEntity(requestDTO);

        Food food = findFoodById(requestDTO.getFoodId());

        reel.setFood(food);

        Reel savedReel = reelRepository.save(reel);

        return reelMapper.toResponseDTO(savedReel);
    }

    // Get all reels
    public List<ReelResponseDTO> getAllReels() {

        return reelRepository.findAll()
                .stream()
                .map(reelMapper::toResponseDTO)
                .toList();
    }

    // Get reel by ID
    public ReelResponseDTO getReelById(Long id) {

        Reel reel = findReelById(id);

        return reelMapper.toResponseDTO(reel);
    }

    // Get all reels for a food
    public List<ReelResponseDTO> getReelsByFoodId(Long foodId) {

        // Ensure the food exists
        findFoodById(foodId);

        return reelRepository.findByFoodId(foodId)
                .stream()
                .map(reelMapper::toResponseDTO)
                .toList();
    }

    // Update reel
    public ReelResponseDTO updateReel(
            Long id,
            ReelRequestDTO requestDTO) {

        Reel existingReel = findReelById(id);

        Food food = findFoodById(requestDTO.getFoodId());

        existingReel.setVideoUrl(requestDTO.getVideoUrl());
        existingReel.setThumbnailUrl(requestDTO.getThumbnailUrl());
        existingReel.setCaption(requestDTO.getCaption());
        existingReel.setFood(food);

        Reel updatedReel = reelRepository.save(existingReel);

        return reelMapper.toResponseDTO(updatedReel);
    }

    // Delete reel
    public void deleteReel(Long id) {

        Reel reel = findReelById(id);

        reelRepository.delete(reel);
    }

    // Internal helper
    private Reel findReelById(Long id) {

        return reelRepository.findById(id)
                .orElseThrow(() -> new ReelNotFoundException(
                        "Reel not found with id: " + id));
    }

    // Internal helper
    private Food findFoodById(Long id) {

        return foodRepository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException(
                        "Food not found with id: " + id));
    }
}