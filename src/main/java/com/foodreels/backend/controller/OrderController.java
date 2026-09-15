package com.foodreels.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.CreateOrderRequestDTO;
import com.foodreels.backend.dto.OrderResponseDTO;
import com.foodreels.backend.dto.UpdateOrderStatusRequestDTO;
import com.foodreels.backend.entity.OrderStatus;
import com.foodreels.backend.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Orders",
        description = "Order creation, history, restaurant management and status workflow"
)
public class OrderController {

    private final OrderService orderService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OrderController(
            OrderService orderService) {

        this.orderService =
                orderService;
    }


    // =========================================================
    // CREATE ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<OrderResponseDTO>
            createOrder(

                    @AuthenticationPrincipal
                    Jwt jwt,

                    @Valid
                    @RequestBody
                    CreateOrderRequestDTO request) {


        OrderResponseDTO response =
                orderService
                        .createOrder(
                                jwt.getSubject(),
                                request
                        );


        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        response
                );
    }


    // =========================================================
    // MY ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<
            Page<OrderResponseDTO>>
            getMyOrders(

                    @AuthenticationPrincipal
                    Jwt jwt,

                    @RequestParam(
                            required = false
                    )
                    OrderStatus status,

                    @RequestParam(
                            defaultValue = "0"
                    )
                    int page,

                    @RequestParam(
                            defaultValue = "10"
                    )
                    int size) {


        return ResponseEntity.ok(

                orderService
                        .getMyOrders(
                                jwt.getSubject(),
                                status,
                                page,
                                size
                        )
        );
    }


    // =========================================================
    // RESTAURANT ORDERS
    // =========================================================

    @GetMapping(
            "/restaurant/{restaurantId}"
    )
    public ResponseEntity<
            Page<OrderResponseDTO>>
            getRestaurantOrders(

                    @PathVariable
                    Long restaurantId,

                    @RequestParam(
                            required = false
                    )
                    OrderStatus status,

                    @RequestParam(
                            defaultValue = "0"
                    )
                    int page,

                    @RequestParam(
                            defaultValue = "20"
                    )
                    int size) {


        return ResponseEntity.ok(

                orderService
                        .getRestaurantOrders(
                                restaurantId,
                                status,
                                page,
                                size
                        )
        );
    }


    // =========================================================
    // GET ONE OF MY ORDERS
    // =========================================================

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO>
            getMyOrder(

                    @AuthenticationPrincipal
                    Jwt jwt,

                    @PathVariable
                    Long orderId) {


        return ResponseEntity.ok(

                orderService
                        .getMyOrder(
                                jwt.getSubject(),
                                orderId
                        )
        );
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @PatchMapping(
            "/{orderId}/status"
    )
    public ResponseEntity<OrderResponseDTO>
            updateOrderStatus(

                    @PathVariable
                    Long orderId,

                    @Valid
                    @RequestBody
                    UpdateOrderStatusRequestDTO request) {


        return ResponseEntity.ok(

                orderService
                        .updateOrderStatus(
                                orderId,
                                request.getStatus()
                        )
        );
    }


    // =========================================================
    // CANCEL MY ORDER
    // =========================================================

    @PostMapping(
            "/{orderId}/cancel"
    )
    public ResponseEntity<OrderResponseDTO>
            cancelMyOrder(

                    @AuthenticationPrincipal
                    Jwt jwt,

                    @PathVariable
                    Long orderId) {


        return ResponseEntity.ok(

                orderService
                        .cancelMyOrder(
                                jwt.getSubject(),
                                orderId
                        )
        );
    }
}