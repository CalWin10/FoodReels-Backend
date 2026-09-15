package com.foodreels.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.Order;
import com.foodreels.backend.entity.OrderStatus;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    // =========================================================
    // USER ORDERS
    // =========================================================

    Page<Order> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            OrderStatus status,
            Pageable pageable
    );


    // =========================================================
    // RESTAURANT ORDERS
    // =========================================================

    Page<Order> findByRestaurantIdOrderByCreatedAtDesc(
            Long restaurantId,
            Pageable pageable
    );

    Page<Order> findByRestaurantIdAndStatusOrderByCreatedAtDesc(
            Long restaurantId,
            OrderStatus status,
            Pageable pageable
    );
}