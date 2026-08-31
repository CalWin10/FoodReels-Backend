package com.foodreels.backend.controller;

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