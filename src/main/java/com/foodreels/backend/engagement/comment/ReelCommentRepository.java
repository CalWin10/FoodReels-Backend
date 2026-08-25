package com.foodreels.backend.engagement.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReelCommentRepository
        extends JpaRepository<ReelComment, Long> {

    List<ReelComment> findByReel_IdOrderByCreatedAtDesc(
            Long reelId
    );

    long countByReel_Id(Long reelId);
}

