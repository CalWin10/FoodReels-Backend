package com.foodreels.backend.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.foodreels.backend.dto.CreateOrderRequestDTO;
import com.foodreels.backend.dto.OrderItemRequestDTO;
import com.foodreels.backend.dto.OrderItemResponseDTO;
import com.foodreels.backend.dto.OrderResponseDTO;
import com.foodreels.backend.entity.Food;
import com.foodreels.backend.entity.Order;
import com.foodreels.backend.entity.OrderItem;
import com.foodreels.backend.entity.OrderStatus;
import com.foodreels.backend.entity.Restaurant;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.repository.FoodRepository;
import com.foodreels.backend.repository.OrderRepository;
import com.foodreels.backend.repository.RestaurantRepository;
import com.foodreels.backend.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final FoodRepository foodRepository;

    private final RestaurantRepository restaurantRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            FoodRepository foodRepository,
            RestaurantRepository restaurantRepository) {

        this.orderRepository =
                orderRepository;

        this.userRepository =
                userRepository;

        this.foodRepository =
                foodRepository;

        this.restaurantRepository =
                restaurantRepository;
    }


    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Transactional
    public OrderResponseDTO createOrder(
            String email,
            CreateOrderRequestDTO request) {

        // -----------------------------------------------------
        // FIND AUTHENTICATED USER
        // -----------------------------------------------------

        User user =
                findUserByEmail(
                        email
                );


        // -----------------------------------------------------
        // VALIDATE ORDER ITEMS
        // -----------------------------------------------------

        if (request.getItems() == null
                ||
                request.getItems().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order must contain at least one item"
            );
        }


        // -----------------------------------------------------
        // VALIDATE DELIVERY ADDRESS
        // -----------------------------------------------------

        if (request.getDeliveryAddress() == null
                ||
                request.getDeliveryAddress()
                        .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Delivery address is required"
            );
        }


        // -----------------------------------------------------
        // CREATE ORDER
        // -----------------------------------------------------

        Order order =
                new Order();

        order.setUser(
                user
        );

        order.setDeliveryAddress(
                request.getDeliveryAddress()
                        .trim()
        );

        order.setStatus(
                OrderStatus.CREATED
        );


        // -----------------------------------------------------
        // ORDER TOTAL
        // -----------------------------------------------------

        BigDecimal orderTotal =
                BigDecimal.ZERO;


        // -----------------------------------------------------
        // ONE ORDER = ONE RESTAURANT
        // -----------------------------------------------------

        Restaurant orderRestaurant =
                null;


        // =====================================================
        // MERGE DUPLICATE FOOD ITEMS
        // =====================================================

        Map<Long, Integer> mergedItems =
                new LinkedHashMap<>();


        for (OrderItemRequestDTO itemRequest
                : request.getItems()) {


            // -------------------------------------------------
            // FOOD ID VALIDATION
            // -------------------------------------------------

            if (itemRequest.getFoodId() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "foodId is required"
                );
            }


            // -------------------------------------------------
            // QUANTITY VALIDATION
            // -------------------------------------------------

            if (itemRequest.getQuantity() == null
                    ||
                    itemRequest.getQuantity() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Quantity must be greater than 0"
                );
            }


            // -------------------------------------------------
            // MERGE DUPLICATE ITEMS
            // -------------------------------------------------

            mergedItems.merge(
                    itemRequest.getFoodId(),
                    itemRequest.getQuantity(),
                    Integer::sum
            );
        }


        // =====================================================
        // PROCESS MERGED ORDER ITEMS
        // =====================================================

        for (Map.Entry<Long, Integer> entry
                : mergedItems.entrySet()) {


            Long foodId =
                    entry.getKey();


            Integer itemQuantity =
                    entry.getValue();


            // -------------------------------------------------
            // FIND FOOD
            // -------------------------------------------------

            Food food =
                    foodRepository
                            .findById(
                                    foodId
                            )
                            .orElseThrow(
                                    () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,

                                                "Food not found: "
                                                        + foodId
                                        )
                            );


            // -------------------------------------------------
            // GET RESTAURANT
            // -------------------------------------------------

            Restaurant foodRestaurant =
                    food.getRestaurant();


            if (foodRestaurant == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Food is not linked to a restaurant"
                );
            }


            // =================================================
            // CHECK SAME RESTAURANT
            // =================================================

            if (orderRestaurant == null) {

                // First food decides the restaurant

                orderRestaurant =
                        foodRestaurant;

                order.setRestaurant(
                        orderRestaurant
                );

            } else if (
                    !orderRestaurant
                            .getId()
                            .equals(
                                    foodRestaurant
                                            .getId()
                            )
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "All food items must belong to the same restaurant"
                );
            }


            // =================================================
            // SECURE PRICE LOOKUP
            // =================================================
            //
            // PRICE IS READ FROM DATABASE.
            //
            // Client cannot control:
            //
            // unitPrice
            // subtotal
            // totalAmount
            //
            // =================================================

            BigDecimal unitPrice =
                    BigDecimal.valueOf(
                            food.getPrice()
                    );


            BigDecimal quantity =
                    BigDecimal.valueOf(
                            itemQuantity
                    );


            BigDecimal subtotal =
                    unitPrice.multiply(
                            quantity
                    );


            // =================================================
            // CREATE ORDER ITEM
            // =================================================

            OrderItem orderItem =
                    new OrderItem();


            orderItem.setFood(
                    food
            );


            orderItem.setQuantity(
                    itemQuantity
            );


            orderItem.setUnitPrice(
                    unitPrice
            );


            orderItem.setSubtotal(
                    subtotal
            );


            // addItem() also connects:
            //
            // orderItem.setOrder(order)

            order.addItem(
                    orderItem
            );


            // =================================================
            // ADD TO ORDER TOTAL
            // =================================================

            orderTotal =
                    orderTotal.add(
                            subtotal
                    );
        }


        // -----------------------------------------------------
        // SET FINAL TOTAL
        // -----------------------------------------------------

        order.setTotalAmount(
                orderTotal
        );


        // -----------------------------------------------------
        // SAVE ORDER
        // -----------------------------------------------------
        //
        // OrderItem uses CascadeType.ALL,
        // therefore the items are saved automatically.
        //
        // -----------------------------------------------------

        Order savedOrder =
                orderRepository.save(
                        order
                );


        // -----------------------------------------------------
        // RETURN DTO
        // -----------------------------------------------------

        return toResponseDTO(
                savedOrder
        );
    }


    // =========================================================
    // MY ORDERS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO>
            getMyOrders(

                    String email,
                    OrderStatus status,
                    int page,
                    int size) {


        User user =
                findUserByEmail(
                        email
                );


        page =
                normalizePage(
                        page
                );


        size =
                normalizeSize(
                        size
                );


        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );


        Page<Order> orders;


        // -----------------------------------------------------
        // WITHOUT STATUS FILTER
        // -----------------------------------------------------

        if (status == null) {

            orders =
                    orderRepository
                            .findByUserIdOrderByCreatedAtDesc(
                                    user.getId(),
                                    pageable
                            );

        }

        // -----------------------------------------------------
        // WITH STATUS FILTER
        // -----------------------------------------------------

        else {

            orders =
                    orderRepository
                            .findByUserIdAndStatusOrderByCreatedAtDesc(
                                    user.getId(),
                                    status,
                                    pageable
                            );
        }


        return orders.map(
                this::toResponseDTO
        );
    }


    // =========================================================
    // GET ONE OF MY ORDERS
    // =========================================================

    @Transactional(readOnly = true)
    public OrderResponseDTO getMyOrder(
            String email,
            Long orderId) {


        Order order =
                findOrderById(
                        orderId
                );


        // -----------------------------------------------------
        // OWNERSHIP CHECK
        // -----------------------------------------------------

        if (!order.getUser()
                .getEmail()
                .equalsIgnoreCase(
                        email
                )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this order"
            );
        }


        return toResponseDTO(
                order
        );
    }


    // =========================================================
    // RESTAURANT ORDERS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO>
            getRestaurantOrders(

                    Long restaurantId,
                    OrderStatus status,
                    int page,
                    int size) {


        // -----------------------------------------------------
        // CHECK RESTAURANT EXISTS
        // -----------------------------------------------------

        Restaurant restaurant = restaurantRepository
                .findById(
                        restaurantId
                )
                .orElseThrow(
                        () ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Restaurant not found"
                            )
                );


        validateRestaurantAccess(restaurant);

        page =
                normalizePage(
                        page
                );


        size =
                normalizeSize(
                        size
                );


        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );


        Page<Order> orders;


        // -----------------------------------------------------
        // WITHOUT STATUS FILTER
        // -----------------------------------------------------

        if (status == null) {

            orders =
                    orderRepository
                            .findByRestaurantIdOrderByCreatedAtDesc(
                                    restaurantId,
                                    pageable
                            );

        }

        // -----------------------------------------------------
        // WITH STATUS FILTER
        // -----------------------------------------------------

        else {

            orders =
                    orderRepository
                            .findByRestaurantIdAndStatusOrderByCreatedAtDesc(
                                    restaurantId,
                                    status,
                                    pageable
                            );
        }


        return orders.map(
                this::toResponseDTO
        );
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Transactional
    public OrderResponseDTO updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {


        // -----------------------------------------------------
        // VALIDATE STATUS
        // -----------------------------------------------------

        if (newStatus == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order status is required"
            );
        }


        // -----------------------------------------------------
        // FIND ORDER
        // -----------------------------------------------------

        Order order =
                findOrderById(
                        orderId
                );


        validateRestaurantAccess(order.getRestaurant());

        OrderStatus currentStatus =
                order.getStatus();


        // -----------------------------------------------------
        // SAME STATUS
        // -----------------------------------------------------
        //
        // We simply return the current order.
        //
        // -----------------------------------------------------

        if (currentStatus == newStatus) {

            return toResponseDTO(
                    order
            );
        }


        // -----------------------------------------------------
        // VALIDATE STATUS TRANSITION
        // -----------------------------------------------------

        if (!isValidTransition(
                currentStatus,
                newStatus
        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,

                    "Invalid order status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }


        // -----------------------------------------------------
        // UPDATE STATUS
        // -----------------------------------------------------

        order.setStatus(
                newStatus
        );


        Order savedOrder =
                orderRepository.save(
                        order
                );


        return toResponseDTO(
                savedOrder
        );
    }


    // =========================================================
    // CUSTOMER CANCEL ORDER
    // =========================================================

    @Transactional
    public OrderResponseDTO cancelMyOrder(
            String email,
            Long orderId) {


        // -----------------------------------------------------
        // FIND ORDER
        // -----------------------------------------------------

        Order order =
                findOrderById(
                        orderId
                );


        // -----------------------------------------------------
        // OWNERSHIP CHECK
        // -----------------------------------------------------

        if (!order.getUser()
                .getEmail()
                .equalsIgnoreCase(
                        email
                )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to cancel this order"
            );
        }


        // -----------------------------------------------------
        // CUSTOMER CAN CANCEL ONLY CREATED ORDER
        // -----------------------------------------------------

        if (order.getStatus()
                != OrderStatus.CREATED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,

                    "Order can only be cancelled while status is CREATED"
            );
        }


        // -----------------------------------------------------
        // CANCEL ORDER
        // -----------------------------------------------------

        order.setStatus(
                OrderStatus.CANCELLED
        );


        Order savedOrder =
                orderRepository.save(
                        order
                );


        return toResponseDTO(
                savedOrder
        );
    }


    // =========================================================
    // STATUS WORKFLOW VALIDATION
    // =========================================================

    private boolean isValidTransition(
            OrderStatus current,
            OrderStatus next) {


        return switch (current) {


            // -------------------------------------------------
            // CREATED
            // -------------------------------------------------

            case CREATED ->

                    next == OrderStatus.CONFIRMED
                    ||
                    next == OrderStatus.CANCELLED;


            // -------------------------------------------------
            // CONFIRMED
            // -------------------------------------------------

            case CONFIRMED ->

                    next == OrderStatus.PREPARING
                    ||
                    next == OrderStatus.CANCELLED;


            // -------------------------------------------------
            // PREPARING
            // -------------------------------------------------

            case PREPARING ->

                    next == OrderStatus.READY;


            // -------------------------------------------------
            // READY
            // -------------------------------------------------

            case READY ->

                    next == OrderStatus.OUT_FOR_DELIVERY;


            // -------------------------------------------------
            // OUT FOR DELIVERY
            // -------------------------------------------------

            case OUT_FOR_DELIVERY ->

                    next == OrderStatus.DELIVERED;


            // -------------------------------------------------
            // TERMINAL STATES
            // -------------------------------------------------

            case DELIVERED,
                 CANCELLED ->

                    false;
        };
    }


    // =========================================================
    // RESTAURANT ORDER ACCESS
    // =========================================================

    private void validateRestaurantAccess(Restaurant restaurant) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Restaurant access denied");
        }
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (admin) {
            return;
        }
        if (restaurant.getOwner() == null
                || !authentication.getName().equalsIgnoreCase(restaurant.getOwner().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this restaurant");
        }
    }

    // =========================================================
    // FIND USER
    // =========================================================

    private User findUserByEmail(
            String email) {


        return userRepository
                .findByEmail(
                        email
                )
                .orElseThrow(
                        () ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "User not found"
                            )
                );
    }


    // =========================================================
    // FIND ORDER
    // =========================================================

    private Order findOrderById(
            Long orderId) {


        return orderRepository
                .findById(
                        orderId
                )
                .orElseThrow(
                        () ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Order not found"
                            )
                );
    }


    // =========================================================
    // NORMALIZE PAGE
    // =========================================================

    private int normalizePage(
            int page) {


        if (page < 0) {

            return 0;
        }


        return page;
    }


    // =========================================================
    // NORMALIZE SIZE
    // =========================================================

    private int normalizeSize(
            int size) {


        if (size <= 0) {

            return 10;
        }


        if (size > 50) {

            return 50;
        }


        return size;
    }


    // =========================================================
    // ORDER → RESPONSE DTO
    // =========================================================

    private OrderResponseDTO toResponseDTO(
            Order order) {


        List<OrderItemResponseDTO> items =
                order.getItems()
                        .stream()
                        .map(
                                this::toItemResponseDTO
                        )
                        .toList();


        return new OrderResponseDTO(

                // ORDER ID
                order.getId(),


                // USER ID
                order.getUser()
                        .getId(),


                // RESTAURANT ID
                order.getRestaurant()
                        .getId(),


                // RESTAURANT NAME
                order.getRestaurant()
                        .getName(),


                // ORDER ITEMS
                items,


                // TOTAL
                order.getTotalAmount(),


                // STATUS
                order.getStatus(),


                // DELIVERY ADDRESS
                order.getDeliveryAddress(),


                // CREATED AT
                order.getCreatedAt(),


                // UPDATED AT
                order.getUpdatedAt()
        );
    }


    // =========================================================
    // ORDER ITEM → RESPONSE DTO
    // =========================================================

    private OrderItemResponseDTO
            toItemResponseDTO(
                    OrderItem item) {


        return new OrderItemResponseDTO(

                // ORDER ITEM ID
                item.getId(),


                // FOOD ID
                item.getFood()
                        .getId(),


                // FOOD NAME
                item.getFood()
                        .getName(),


                // QUANTITY
                item.getQuantity(),


                // PRICE WHEN ORDER WAS CREATED
                item.getUnitPrice(),


                // QUANTITY × UNIT PRICE
                item.getSubtotal()
        );
    }
}
