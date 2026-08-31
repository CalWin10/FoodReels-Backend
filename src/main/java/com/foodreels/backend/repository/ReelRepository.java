package com.foodreels.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodreels.backend.entity.Reel;

public interface ReelRepository
        extends JpaRepository<Reel, Long> {

    List<Reel> findByFoodId(Long foodId);

    // Phase 3 discovery
    @Query("""
        SELECT r
        FROM Reel r
        WHERE
            (:q IS NULL
                OR LOWER(r.caption)
                    LIKE LOWER(CONCAT('%', :q, '%')))
            AND (
                :restaurantId IS NULL
                OR r.food.restaurant.id = :restaurantId
            )
            AND (
                :foodId IS NULL
                OR r.food.id = :foodId
            )
            AND (
                :category IS NULL
                OR LOWER(r.food.category)
                    = LOWER(:category)
            )
    """)
    Page<Reel> discoverReels(
            @Param("q") String q,
            @Param("restaurantId") Long restaurantId,
            @Param("foodId") Long foodId,
            @Param("category") String category,
            Pageable pageable
    );

    // Phase 7 advanced search
    @Query("""
        SELECT r
        FROM Reel r
        WHERE
            (
                :q = ''
                OR LOWER(r.caption)
                    LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(r.food.name)
                    LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(r.food.category)
                    LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(r.food.restaurant.name)
                    LIKE LOWER(CONCAT('%', :q, '%'))
            )
            AND (
                :category = ''
                OR LOWER(r.food.category)
                    = LOWER(:category)
            )
            AND (
                :restaurantId = 0
                OR r.food.restaurant.id = :restaurantId
            )
            AND (
                :foodId = 0
                OR r.food.id = :foodId
            )
    """)
    Page<Reel> searchReels(
            @Param("q") String q,
            @Param("category") String category,
            @Param("restaurantId") Long restaurantId,
            @Param("foodId") Long foodId,
            Pageable pageable
    );
}