package com.foodreels.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.ReelComment;

public interface ReelCommentRepository
        extends JpaRepository<ReelComment, Long> {

    List<ReelComment> findByReel_IdOrderByCreatedAtDesc(
            Long reelId
    );
    List<ReelComment> findByUser_Id(Long userId);

    long countByReel_Id(Long reelId);
}