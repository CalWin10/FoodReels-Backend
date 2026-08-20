package com.foodreels.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.Reel;

public interface ReelRepository extends JpaRepository<Reel, Long> {
    List<Reel> findByFoodId(Long foodId);
}
