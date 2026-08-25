package com.foodreels.backend.reel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodreels.backend.reel.Reel;

public interface ReelRepository extends JpaRepository<Reel, Long> {
    List<Reel> findByFoodId(Long foodId);

    @Query("""
            SELECT r FROM Reel r
            WHERE (:q IS NULL
                   OR LOWER(r.caption) LIKE LOWER(CONCAT('%', :q, '%')))

              AND (:restaurantId IS NULL
                   OR r.food.restaurant.id = :restaurantId)

              AND (:foodId IS NULL
                   OR r.food.id = :foodId)

              AND (:category IS NULL
                   OR LOWER(r.food.category) = LOWER(:category))
            """)
    Page<Reel> discoverReels(
            @Param("q") String q,
            @Param("restaurantId") Long restaurantId,
            @Param("foodId") Long foodId,
            @Param("category") String category,
            Pageable pageable);
}


