package com.foodreels.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.OrderItem;

public interface OrderItemRepository
        extends JpaRepository<
                OrderItem,
                Long> {

    List<OrderItem>
            findByOrderId(
                    Long orderId
            );
}