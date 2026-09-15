package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.FoodResponseDTO;
import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.dto.RestaurantResponseDTO;
import com.foodreels.backend.dto.UnifiedSearchResponseDTO;
import com.foodreels.backend.service.SearchService;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "FoodReels search APIs for foods, restaurants and reels.")
@SecurityRequirement(name = "bearerAuth")
public class SearchController {

    private final SearchService searchService;

    public SearchController(
            SearchService searchService) {

        this.searchService =
                searchService;
    }

    // =========================================================
    // UNIFIED SEARCH
    // =========================================================

    @Operation(
            summary = "Search FoodReels",
            description = "Searches foods, restaurants and reels using the query and pagination parameters.",
            parameters = {
                    @Parameter(name = "q", description = "Search text", example = "pizza"),
                    @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<UnifiedSearchResponseDTO>
            unifiedSearch(

                    @RequestParam
                    String q,

                    @RequestParam(
                            defaultValue = "0"
                    )
                    int page,

                    @RequestParam(
                            defaultValue = "5"
                    )
                    int size) {

        return ResponseEntity.ok(
                searchService.unifiedSearch(
                        q,
                        page,
                        size
                )
        );
    }

    // =========================================================
    // FOOD SEARCH
    // =========================================================

    @Operation(
            summary = "Search food items",
            description = "Returns paginated food items with optional text, category, restaurant and price filters and sorting.",
            parameters = {
                    @Parameter(name = "q", description = "Search text", example = "pizza"),
                    @Parameter(name = "category", description = "Food category", example = "Pizza"),
                    @Parameter(name = "restaurantId", description = "Restaurant ID", example = "1"),
                    @Parameter(name = "minPrice", description = "Minimum food price", example = "100"),
                    @Parameter(name = "maxPrice", description = "Maximum food price", example = "500"),
                    @Parameter(name = "sort", description = "Sort order: newest, oldest, price_asc, price_desc or name", example = "newest"),
                    @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/foods")
    public ResponseEntity<Page<FoodResponseDTO>>
            searchFoods(

                    @RequestParam(
                            required = false
                    )
                    String q,

                    @RequestParam(
                            required = false
                    )
                    String category,

                    @RequestParam(
                            required = false
                    )
                    Long restaurantId,

                    @RequestParam(
                            required = false
                    )
                    Double minPrice,

                    @RequestParam(
                            required = false
                    )
                    Double maxPrice,

                    @RequestParam(
                            defaultValue = "newest"
                    )
                    String sort,

                    @RequestParam(
                            defaultValue = "0"
                    )
                    int page,

                    @RequestParam(
                            defaultValue = "10"
                    )
                    int size) {

        return ResponseEntity.ok(
                searchService.searchFoods(
                        q,
                        category,
                        restaurantId,
                        minPrice,
                        maxPrice,
                        sort,
                        page,
                        size
                )
        );
    }

    // =========================================================
    // RESTAURANT SEARCH
    // =========================================================

    @Operation(
            summary = "Search restaurants",
            description = "Returns paginated restaurants with optional search text, minimum rating and sorting.",
            parameters = {
                    @Parameter(name = "q", description = "Search text", example = "pizza"),
                    @Parameter(name = "minRating", description = "Minimum restaurant rating", example = "4"),
                    @Parameter(name = "sort", description = "Sort order: newest, oldest, rating or name", example = "newest"),
                    @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/restaurants")
    public ResponseEntity<Page<RestaurantResponseDTO>>
            searchRestaurants(

                    @RequestParam(
                            required = false
                    )
                    String q,

                    @RequestParam(
                            required = false
                    )
                    Double minRating,

                    @RequestParam(
                            defaultValue = "newest"
                    )
                    String sort,

                    @RequestParam(
                            defaultValue = "0"
                    )
                    int page,

                    @RequestParam(
                            defaultValue = "10"
                    )
                    int size) {

        return ResponseEntity.ok(
                searchService.searchRestaurants(
                        q,
                        minRating,
                        sort,
                        page,
                        size
                )
        );
    }

    // =========================================================
    // REEL SEARCH
    // =========================================================

    @Operation(
            summary = "Search reels",
            description = "Returns paginated reels with optional text, category, restaurant and food filters and sorting.",
            parameters = {
                    @Parameter(name = "q", description = "Search text", example = "pizza"),
                    @Parameter(name = "category", description = "Food category", example = "Pizza"),
                    @Parameter(name = "restaurantId", description = "Restaurant ID", example = "1"),
                    @Parameter(name = "foodId", description = "Food item ID", example = "1"),
                    @Parameter(name = "sort", description = "Sort order: newest, oldest or popular", example = "newest"),
                    @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                    @Parameter(name = "size", description = "Number of results per page", example = "10")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/reels")
    public ResponseEntity<Page<ReelResponseDTO>>
            searchReels(

                    @RequestParam(
                            required = false
                    )
                    String q,

                    @RequestParam(
                            required = false
                    )
                    String category,

                    @RequestParam(
                            required = false
                    )
                    Long restaurantId,

                    @RequestParam(
                            required = false
                    )
                    Long foodId,

                    @RequestParam(
                            defaultValue = "newest"
                    )
                    String sort,

                    @RequestParam(
                            defaultValue = "0"
                    )
                    int page,

                    @RequestParam(
                            defaultValue = "10"
                    )
                    int size) {

        return ResponseEntity.ok(
                searchService.searchReels(
                        q,
                        category,
                        restaurantId,
                        foodId,
                        sort,
                        page,
                        size
                )
        );
    }
}