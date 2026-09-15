package com.foodreels.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "orders",
        indexes = {

            // User order history
            @Index(
                    name = "idx_orders_user_created",
                    columnList = "user_id, created_at"
            ),

            // Restaurant order dashboard
            @Index(
                    name = "idx_orders_restaurant_created",
                    columnList = "restaurant_id, created_at"
            ),

            // User order status filtering
            @Index(
                    name = "idx_orders_user_status",
                    columnList = "user_id, status"
            ),

            // Restaurant order status filtering
            @Index(
                    name = "idx_orders_restaurant_status",
                    columnList = "restaurant_id, status"
            )
        }
)
public class Order {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    // =========================================================
    // USER
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;


    // =========================================================
    // RESTAURANT
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "restaurant_id",
            nullable = false
    )
    private Restaurant restaurant;


    // =========================================================
    // ORDER ITEMS
    // =========================================================

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items =
            new ArrayList<>();


    // =========================================================
    // TOTAL AMOUNT
    // =========================================================

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount;


    // =========================================================
    // ORDER STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private OrderStatus status;


    // =========================================================
    // DELIVERY ADDRESS
    // =========================================================

    @Column(
            name = "delivery_address",
            nullable = false,
            length = 500
    )
    private String deliveryAddress;


    // =========================================================
    // CREATED AT
    // =========================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    // =========================================================
    // UPDATED AT
    // =========================================================

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Order() {
    }


    // =========================================================
    // PRE PERSIST
    // =========================================================

    @PrePersist
    public void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        this.createdAt =
                now;

        this.updatedAt =
                now;


        if (this.status == null) {

            this.status =
                    OrderStatus.CREATED;
        }


        if (this.totalAmount == null) {

            this.totalAmount =
                    BigDecimal.ZERO;
        }
    }


    // =========================================================
    // PRE UPDATE
    // =========================================================

    @PreUpdate
    public void onUpdate() {

        this.updatedAt =
                LocalDateTime.now();
    }


    // =========================================================
    // ADD ORDER ITEM
    // =========================================================

    public void addItem(
            OrderItem item) {

        this.items.add(
                item
        );

        item.setOrder(
                this
        );
    }


    // =========================================================
    // REMOVE ORDER ITEM
    // =========================================================

    public void removeItem(
            OrderItem item) {

        this.items.remove(
                item
        );

        item.setOrder(
                null
        );
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(
            Long id) {

        this.id =
                id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(
            User user) {

        this.user =
                user;
    }


    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(
            Restaurant restaurant) {

        this.restaurant =
                restaurant;
    }


    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItem> items) {

        this.items =
                items;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status) {

        this.status =
                status;
    }


    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(
            String deliveryAddress) {

        this.deliveryAddress =
                deliveryAddress;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt =
                updatedAt;
    }
}