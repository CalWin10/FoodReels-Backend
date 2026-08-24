package com.foodreels.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.ReelSave;

public interface ReelSaveRepository
        extends JpaRepository<ReelSave, Long> {

    boolean existsByUser_IdAndReel_Id(
            Long userId,
            Long reelId
    );

    Optional<ReelSave> findByUser_IdAndReel_Id(
            Long userId,
            Long reelId
    );

    List<ReelSave> findByUser_IdOrderByCreatedAtDesc(
            Long userId
    );

    long countByUser_Id(Long userId);
}