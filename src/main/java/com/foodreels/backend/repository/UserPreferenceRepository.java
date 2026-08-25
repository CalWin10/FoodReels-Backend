package com.foodreels.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.UserPreference;

public interface UserPreferenceRepository
        extends JpaRepository<UserPreference, Long> {

    boolean existsByUser_IdAndCategoryIgnoreCase(
            Long userId,
            String category
    );

    Optional<UserPreference>
            findByUser_IdAndCategoryIgnoreCase(
                    Long userId,
                    String category
            );

    List<UserPreference>
            findByUser_IdOrderByCreatedAtAsc(
                    Long userId
            );
}