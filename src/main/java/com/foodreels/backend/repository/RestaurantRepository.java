package com.foodreels.backend.repository;

import java.util.List;
import java.util.Optional;

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

    @Query("""
                SELECT r
                FROM Restaurant r
                WHERE
                    r.latitude IS NOT NULL
                    AND r.longitude IS NOT NULL
                    AND r.latitude BETWEEN :minLat AND :maxLat
                    AND r.longitude BETWEEN :minLng AND :maxLng
            """)
    List<Restaurant> findNearbyCandidates(

            @Param("minLat") double minLat,

            @Param("maxLat") double maxLat,

            @Param("minLng") double minLng,

            @Param("maxLng") double maxLng);

    boolean existsByIdAndOwnerEmail(
            Long restaurantId,
            String email);

    Optional<Restaurant> findByIdAndOwnerEmail(
            Long restaurantId,
            String email);
}