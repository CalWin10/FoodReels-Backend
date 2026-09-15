package com.foodreels.backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "order_items",
        indexes = {

            @Index(
                    name = "idx_order_items_order",
                    columnList = "order_id"
            ),

            @Index(
                    name = "idx_order_items_food",
                    columnList = "food_id"
            )
        }
)
public class OrderItem {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    // =========================================================
    // ORDER
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;


    // =========================================================
    // FOOD
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "food_id",
            nullable = false
    )
    private Food food;


    // =========================================================
    // QUANTITY
    // =========================================================

    @Column(
            nullable = false
    )
    private Integer quantity;


    // =========================================================
    // UNIT PRICE
    // =========================================================
    //
    // This is a PRICE SNAPSHOT.
    //
    // Even if the Food price changes later,
    // historical orders keep the original price.
    //
    // =========================================================

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal unitPrice;


    // =========================================================
    // SUBTOTAL
    // =========================================================

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal subtotal;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public OrderItem() {
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


    public Order getOrder() {
        return order;
    }

    public void setOrder(
            Order order) {

        this.order =
                order;
    }


    public Food getFood() {
        return food;
    }

    public void setFood(
            Food food) {

        this.food =
                food;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Integer quantity) {

        this.quantity =
                quantity;
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(
            BigDecimal unitPrice) {

        this.unitPrice =
                unitPrice;
    }


    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(
            BigDecimal subtotal) {

        this.subtotal =
                subtotal;
    }
}