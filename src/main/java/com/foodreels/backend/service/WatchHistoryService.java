package com.foodreels.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.WatchHistoryResponseDTO;
import com.foodreels.backend.entity.Reel;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.entity.WatchHistory;
import com.foodreels.backend.exception.ReelNotFoundException;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.repository.ReelRepository;
import com.foodreels.backend.repository.UserRepository;
import com.foodreels.backend.repository.WatchHistoryRepository;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class WatchHistoryService {

        private final WatchHistoryRepository watchHistoryRepository;
        private final UserRepository userRepository;
        private final ReelRepository reelRepository;
        private final PersonalizedFeedCacheService cacheService;

        public WatchHistoryService(
                        WatchHistoryRepository watchHistoryRepository,
                        UserRepository userRepository,
                        ReelRepository reelRepository, PersonalizedFeedCacheService cacheService) {

                this.watchHistoryRepository = watchHistoryRepository;
                this.userRepository = userRepository;
                this.reelRepository = reelRepository;
                this.cacheService = cacheService;
        }

        @CacheEvict(value = "personalizedFeed", allEntries = true)
        public void recordWatch(
                        Long reelId,
                        String email) {

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new UserNotFoundException(
                                                "User not found"));

                Reel reel = reelRepository
                                .findById(reelId)
                                .orElseThrow(() -> new ReelNotFoundException(
                                                "Reel not found with id: " + reelId));

                WatchHistory history = watchHistoryRepository
                                .findByUser_IdAndReel_Id(
                                                user.getId(),
                                                reelId)
                                .orElse(null);

                cacheService.evictUserPersonalizedFeed(
                                email);

                LocalDateTime now = LocalDateTime.now();

                if (history == null) {

                        history = new WatchHistory();

                        history.setUser(user);
                        history.setReel(reel);

                        history.setWatchCount(1);

                        history.setFirstWatchedAt(now);
                        history.setLastWatchedAt(now);

                } else {

                        history.setWatchCount(
                                        history.getWatchCount() + 1);

                        history.setLastWatchedAt(now);
                }

                watchHistoryRepository.save(history);
        }

        public List<WatchHistoryResponseDTO> getMyWatchHistory(String email) {

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new UserNotFoundException(
                                                "User not found"));

                return watchHistoryRepository
                                .findByUser_IdOrderByLastWatchedAtDesc(
                                                user.getId())
                                .stream()
                                .map(this::toResponseDTO)
                                .toList();
        }

        private WatchHistoryResponseDTO toResponseDTO(
                        WatchHistory history) {

                WatchHistoryResponseDTO dto = new WatchHistoryResponseDTO();

                dto.setReelId(
                                history.getReel().getId());

                dto.setCaption(
                                history.getReel().getCaption());

                dto.setFoodId(
                                history.getReel()
                                                .getFood()
                                                .getId());

                dto.setFoodName(
                                history.getReel()
                                                .getFood()
                                                .getName());

                dto.setWatchCount(
                                history.getWatchCount());

                dto.setFirstWatchedAt(
                                history.getFirstWatchedAt());

                dto.setLastWatchedAt(
                                history.getLastWatchedAt());

                return dto;
        }
}