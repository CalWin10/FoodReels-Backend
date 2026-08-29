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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.foodreels.backend.dto.ViewResponseDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import com.foodreels.backend.dto.ReelFeedPageDTO;
import org.springframework.cache.annotation.CacheEvict;

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
    @Caching(evict = {
            @CacheEvict(value = "reelFeed", allEntries = true),
            @CacheEvict(value = "personalizedFeed", allEntries = true)
    })
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
    @CacheEvict(value = "reelFeed", allEntries = true)
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
    @CacheEvict(value = "reelFeed", allEntries = true)
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

    @Cacheable(value = "reelFeed", key = "'page:' + #page + ':size:' + #size")
    public ReelFeedPageDTO getReelFeed(
            int page,
            int size) {

        System.out.println(
                "DATABASE FEED METHOD EXECUTED - CACHE MISS");

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"));

        Page<Reel> reels = reelRepository.findAll(pageable);

        List<ReelResponseDTO> content = reels.getContent()
                .stream()
                .map(reelMapper::toResponseDTO)
                .toList();

        return new ReelFeedPageDTO(
                content,
                reels.getTotalElements(),
                reels.getTotalPages(),
                reels.getNumber(),
                reels.getSize(),
                reels.isFirst(),
                reels.isLast());
    }

    public Page<ReelResponseDTO> discoverReels(
            String q,
            Long restaurantId,
            Long foodId,
            String category,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"));

        return reelRepository
                .discoverReels(
                        q,
                        restaurantId,
                        foodId,
                        category,
                        pageable)
                .map(reelMapper::toResponseDTO);
    }

    @CacheEvict(value = "reelFeed", allEntries = true)
    public ViewResponseDTO incrementViewCount(Long reelId) {

        System.out.println(
                "VIEW UPDATED - REEL FEED CACHE SHOULD BE EVICTED");

        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new ReelNotFoundException(
                        "Reel not found with id: " + reelId));

        Long currentViewCount = reel.getViewCount();

        if (currentViewCount == null) {
            currentViewCount = 0L;
        }

        reel.setViewCount(
                currentViewCount + 1);

        Reel updatedReel = reelRepository.save(reel);

        return new ViewResponseDTO(
                updatedReel.getId(),
                updatedReel.getViewCount());
    }
}