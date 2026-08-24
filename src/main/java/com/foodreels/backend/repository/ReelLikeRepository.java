package com.foodreels.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.ReelLike;

public interface ReelLikeRepository
        extends JpaRepository<ReelLike, Long> {

    boolean existsByUser_IdAndReel_Id(
            Long userId,
            Long reelId
    );

    Optional<ReelLike> findByUser_IdAndReel_Id(
            Long userId,
            Long reelId
    );

    long countByReel_Id(Long reelId);
}