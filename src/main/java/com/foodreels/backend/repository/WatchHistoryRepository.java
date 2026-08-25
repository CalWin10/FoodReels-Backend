package com.foodreels.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.WatchHistory;

public interface WatchHistoryRepository
        extends JpaRepository<WatchHistory, Long> {

    Optional<WatchHistory> findByUser_IdAndReel_Id(
            Long userId,
            Long reelId
    );

    List<WatchHistory> findByUser_IdOrderByLastWatchedAtDesc(
            Long userId
    );
}