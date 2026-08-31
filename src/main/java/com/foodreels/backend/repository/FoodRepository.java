package com.foodreels.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodreels.backend.entity.Food;

public interface FoodRepository
        extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(
            Long restaurantId
    );

    // =========================================================
    // PHASE 7 - ADVANCED FOOD SEARCH
    // =========================================================

    @Query("""
        SELECT f
        FROM Food f
        WHERE
            (
                :q = ''
                OR LOWER(f.name)
                    LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(f.description)
                    LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(f.category)
                    LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(f.restaurant.name)
                    LIKE LOWER(CONCAT('%', :q, '%'))
            )

            AND (
                :category = ''
                OR LOWER(f.category)
                    = LOWER(:category)
            )

            AND (
                :restaurantId = 0
                OR f.restaurant.id = :restaurantId
            )

            AND f.price >= :minPrice

            AND f.price <= :maxPrice
    """)
    Page<Food> searchFoods(

            @Param("q")
            String q,

            @Param("category")
            String category,

            @Param("restaurantId")
            Long restaurantId,

            @Param("minPrice")
            Double minPrice,

            @Param("maxPrice")
            Double maxPrice,

            Pageable pageable
    );
}