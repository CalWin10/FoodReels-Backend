package com.foodreels.backend.engagement.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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

