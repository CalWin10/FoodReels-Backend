package com.foodreels.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodreels.backend.entity.Restaurant;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {

    // =========================================================
    // PHASE 7 - ADVANCED RESTAURANT SEARCH
    // =========================================================

    @Query("""
                SELECT r
                FROM Restaurant r
                WHERE
                    (
                        :q = ''
                        OR LOWER(r.name)
                            LIKE LOWER(CONCAT('%', :q, '%'))
                        OR LOWER(r.description)
                            LIKE LOWER(CONCAT('%', :q, '%'))
                        OR LOWER(r.address)
                            LIKE LOWER(CONCAT('%', :q, '%'))
                    )
                    AND r.rating >= :minRating
            """)
    Page<Restaurant> searchRestaurants(
            @Param("q") String q,
            @Param("minRating") Double minRating,
            Pageable pageable);
}