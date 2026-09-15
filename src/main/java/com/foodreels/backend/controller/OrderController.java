package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "Create an order",
            description = "Creates an order for the authenticated user with items from a single restaurant. Food prices and totals are calculated by the backend."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Requested resource or associated user not found")
    })
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

    @Operation(
            summary = "Get my orders",
            description = "Returns paginated orders belonging to the authenticated user with optional status filtering.",
            parameters = {
                    @Parameter(name = "status", description = "Filter orders by status", example = "CREATED"),
                    @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Requested resource or associated user not found")
    })
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

    @Operation(
            summary = "Get restaurant orders",
            description = "Returns paginated orders for the specified restaurant with optional status filtering. Requires the RESTAURANT_OWNER or ADMIN role.",
            parameters = {
                    @Parameter(name = "restaurantId", description = "Restaurant ID", example = "1"),
                    @Parameter(name = "status", description = "Filter orders by status", example = "CREATED"),
                    @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Requested resource or associated user not found")
    })
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

    @Operation(
            summary = "Get one of my orders",
            description = "Returns the specified order if it belongs to the authenticated user.",
            parameters = {
                    @Parameter(name = "orderId", description = "Order ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Requested resource or associated user not found")
    })
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

    @Operation(
            summary = "Update order status",
            description = "Updates an order through the allowed lifecycle transitions. Submitting its current status returns the order unchanged. Requires the RESTAURANT_OWNER or ADMIN role.",
            parameters = {
                    @Parameter(name = "orderId", description = "Order ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Requested resource or associated user not found")
    })
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

    @Operation(
            summary = "Cancel my order",
            description = "Cancels an order belonging to the authenticated user while its status is CREATED.",
            parameters = {
                    @Parameter(name = "orderId", description = "Order ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Requested resource or associated user not found")
    })
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