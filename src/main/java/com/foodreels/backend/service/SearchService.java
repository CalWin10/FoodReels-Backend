package com.foodreels.backend.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.FoodResponseDTO;
import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.dto.RestaurantResponseDTO;
import com.foodreels.backend.dto.UnifiedSearchResponseDTO;
import com.foodreels.backend.entity.Food;
import com.foodreels.backend.entity.Reel;
import com.foodreels.backend.entity.Restaurant;
import com.foodreels.backend.mapper.FoodMapper;
import com.foodreels.backend.mapper.ReelMapper;
import com.foodreels.backend.mapper.RestaurantMapper;
import com.foodreels.backend.repository.FoodRepository;
import com.foodreels.backend.repository.ReelRepository;
import com.foodreels.backend.repository.RestaurantRepository;

@Service
public class SearchService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReelRepository reelRepository;

    private final FoodMapper foodMapper;
    private final RestaurantMapper restaurantMapper;
    private final ReelMapper reelMapper;

    public SearchService(
            FoodRepository foodRepository,
            RestaurantRepository restaurantRepository,
            ReelRepository reelRepository,
            FoodMapper foodMapper,
            RestaurantMapper restaurantMapper,
            ReelMapper reelMapper) {

        this.foodRepository = foodRepository;
        this.restaurantRepository = restaurantRepository;
        this.reelRepository = reelRepository;

        this.foodMapper = foodMapper;
        this.restaurantMapper = restaurantMapper;
        this.reelMapper = reelMapper;
    }

    // =========================================================
    // FOOD SEARCH
    // =========================================================

    public Page<FoodResponseDTO> searchFoods(
            String q,
            String category,
            Long restaurantId,
            Double minPrice,
            Double maxPrice,
            String sort,
            int page,
            int size) {

        page = normalizePage(page);
        size = normalizeSize(size);

        if (q == null || q.isBlank()) {
            q = "";
        } else {
            q = q.trim();
        }

        if (category == null || category.isBlank()) {
            category = "";
        } else {
            category = category.trim();
        }

        if (restaurantId == null) {
            restaurantId = 0L;
        }

        if (minPrice == null) {
            minPrice = 0.0;
        }

        if (maxPrice == null) {
            maxPrice = Double.MAX_VALUE;
        }

        Sort sorting =
                getFoodSort(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sorting
                );

        Page<Food> foods =
                foodRepository.searchFoods(
                        q,
                        category,
                        restaurantId,
                        minPrice,
                        maxPrice,
                        pageable
                );

        return foods.map(
                foodMapper::toResponseDTO
        );
    }

    // =========================================================
    // RESTAURANT SEARCH
    // =========================================================

    public Page<RestaurantResponseDTO> searchRestaurants(
            String q,
            Double minRating,
            String sort,
            int page,
            int size) {

        page = normalizePage(page);
        size = normalizeSize(size);

        if (q == null || q.isBlank()) {
            q = "";
        } else {
            q = q.trim();
        }

        if (minRating == null) {
            minRating = 0.0;
        }

        Sort sorting =
                getRestaurantSort(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sorting
                );

        Page<Restaurant> restaurants =
                restaurantRepository.searchRestaurants(
                        q,
                        minRating,
                        pageable
                );

        return restaurants.map(
                restaurantMapper::toResponseDTO
        );
    }

    // =========================================================
    // REEL SEARCH
    // =========================================================

    public Page<ReelResponseDTO> searchReels(
            String q,
            String category,
            Long restaurantId,
            Long foodId,
            String sort,
            int page,
            int size) {

        page = normalizePage(page);
        size = normalizeSize(size);

        if (q == null || q.isBlank()) {
            q = "";
        } else {
            q = q.trim();
        }

        if (category == null || category.isBlank()) {
            category = "";
        } else {
            category = category.trim();
        }

        if (restaurantId == null) {
            restaurantId = 0L;
        }

        if (foodId == null) {
            foodId = 0L;
        }

        Sort sorting =
                getReelSort(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sorting
                );

        Page<Reel> reels =
                reelRepository.searchReels(
                        q,
                        category,
                        restaurantId,
                        foodId,
                        pageable
                );

        return reels.map(
                reelMapper::toResponseDTO
        );
    }

    // =========================================================
    // UNIFIED SEARCH + REDIS CACHE
    // =========================================================

    @Cacheable(
            value = "searchResults",
            key = "(#q == null ? '' : #q.toLowerCase())"
                    + " + ':page:' + #page"
                    + " + ':size:' + #size"
    )
    public UnifiedSearchResponseDTO unifiedSearch(
            String q,
            int page,
            int size) {

        System.out.println(
                "SEARCH DATABASE EXECUTED - CACHE MISS: "
                        + q
        );

        page = normalizePage(page);
        size = normalizeSize(size);

        String query =
                normalizeText(q);

        if (query == null) {

            return new UnifiedSearchResponseDTO(
                    "",
                    List.of(),
                    0,
                    List.of(),
                    0,
                    List.of(),
                    0
            );
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        // =====================================================
        // FOOD SEARCH
        // =====================================================

        Page<Food> foodPage =
                foodRepository.searchFoods(
                        query,
                        "",
                        0L,
                        0.0,
                        Double.MAX_VALUE,
                        pageable
                );

        List<Food> rankedFoods =
                foodPage.getContent()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        (Food food) ->
                                                calculateFoodScore(
                                                        food,
                                                        query
                                                )
                                ).reversed()
                        )
                        .toList();

        List<FoodResponseDTO> foods =
                rankedFoods.stream()
                        .map(
                                foodMapper::toResponseDTO
                        )
                        .toList();

        // =====================================================
        // RESTAURANT SEARCH
        // =====================================================

        Page<Restaurant> restaurantPage =
                restaurantRepository.searchRestaurants(
                        query,
                        0.0,
                        pageable
                );

        List<Restaurant> rankedRestaurants =
                restaurantPage.getContent()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        (Restaurant restaurant) ->
                                                calculateRestaurantScore(
                                                        restaurant,
                                                        query
                                                )
                                ).reversed()
                        )
                        .toList();

        List<RestaurantResponseDTO> restaurants =
                rankedRestaurants.stream()
                        .map(
                                restaurantMapper::toResponseDTO
                        )
                        .toList();

        // =====================================================
        // REEL SEARCH
        // =====================================================

        Page<Reel> reelPage =
                reelRepository.searchReels(
                        query,
                        "",
                        0L,
                        0L,
                        pageable
                );

        List<Reel> rankedReels =
                reelPage.getContent()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        (Reel reel) ->
                                                calculateReelScore(
                                                        reel,
                                                        query
                                                )
                                ).reversed()
                        )
                        .toList();

        List<ReelResponseDTO> reels =
                rankedReels.stream()
                        .map(
                                reelMapper::toResponseDTO
                        )
                        .toList();

        // =====================================================
        // FINAL UNIFIED RESPONSE
        // =====================================================

        return new UnifiedSearchResponseDTO(
                query,

                foods,
                foodPage.getTotalElements(),

                restaurants,
                restaurantPage.getTotalElements(),

                reels,
                reelPage.getTotalElements()
        );
    }

    // =========================================================
    // FOOD RELEVANCE SCORE
    // =========================================================

    private int calculateFoodScore(
            Food food,
            String query) {

        int score = 0;

        String q =
                query.toLowerCase();

        String name =
                food.getName() == null
                        ? ""
                        : food.getName()
                                .toLowerCase();

        String category =
                food.getCategory() == null
                        ? ""
                        : food.getCategory()
                                .toLowerCase();

        String description =
                food.getDescription() == null
                        ? ""
                        : food.getDescription()
                                .toLowerCase();

        String restaurantName =
                food.getRestaurant() == null
                        || food.getRestaurant().getName() == null
                                ? ""
                                : food.getRestaurant()
                                        .getName()
                                        .toLowerCase();

        if (name.equals(q)) {

            score += 10;

        } else if (name.startsWith(q)) {

            score += 8;

        } else if (name.contains(q)) {

            score += 6;
        }

        if (category.equals(q)) {
            score += 5;
        }

        if (restaurantName.contains(q)) {
            score += 3;
        }

        if (description.contains(q)) {
            score += 2;
        }

        return score;
    }

    // =========================================================
    // RESTAURANT RELEVANCE SCORE
    // =========================================================

    private int calculateRestaurantScore(
            Restaurant restaurant,
            String query) {

        int score = 0;

        String q =
                query.toLowerCase();

        String name =
                restaurant.getName() == null
                        ? ""
                        : restaurant.getName()
                                .toLowerCase();

        String address =
                restaurant.getAddress() == null
                        ? ""
                        : restaurant.getAddress()
                                .toLowerCase();

        String description =
                restaurant.getDescription() == null
                        ? ""
                        : restaurant.getDescription()
                                .toLowerCase();

        if (name.equals(q)) {

            score += 10;

        } else if (name.startsWith(q)) {

            score += 8;

        } else if (name.contains(q)) {

            score += 6;
        }

        if (address.contains(q)) {
            score += 3;
        }

        if (description.contains(q)) {
            score += 2;
        }

        return score;
    }

    // =========================================================
    // REEL RELEVANCE SCORE
    // =========================================================

    private int calculateReelScore(
            Reel reel,
            String query) {

        int score = 0;

        String q =
                query.toLowerCase();

        String caption =
                reel.getCaption() == null
                        ? ""
                        : reel.getCaption()
                                .toLowerCase();

        String foodName =
                reel.getFood() == null
                        || reel.getFood().getName() == null
                                ? ""
                                : reel.getFood()
                                        .getName()
                                        .toLowerCase();

        String category =
                reel.getFood() == null
                        || reel.getFood().getCategory() == null
                                ? ""
                                : reel.getFood()
                                        .getCategory()
                                        .toLowerCase();

        String restaurantName =
                reel.getFood() == null
                        || reel.getFood().getRestaurant() == null
                        || reel.getFood()
                                .getRestaurant()
                                .getName() == null
                                ? ""
                                : reel.getFood()
                                        .getRestaurant()
                                        .getName()
                                        .toLowerCase();

        if (foodName.equals(q)) {

            score += 10;

        } else if (foodName.startsWith(q)) {

            score += 8;

        } else if (foodName.contains(q)) {

            score += 6;
        }

        if (caption.contains(q)) {
            score += 6;
        }

        if (category.equals(q)) {
            score += 5;
        }

        if (restaurantName.contains(q)) {
            score += 3;
        }

        return score;
    }

    // =========================================================
    // TEXT NORMALIZATION
    // =========================================================

    private String normalizeText(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }

    // =========================================================
    // PAGE NORMALIZATION
    // =========================================================

    private int normalizePage(
            int page) {

        if (page < 0) {
            return 0;
        }

        return page;
    }

    // =========================================================
    // SIZE NORMALIZATION
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
    // FOOD SORT
    // =========================================================

    private Sort getFoodSort(
            String sort) {

        if (sort == null) {

            return Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
            );
        }

        return switch (
                sort.toLowerCase()
        ) {

            case "price_asc" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "price"
                    );

            case "price_desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "price"
                    );

            case "name" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "name"
                    );

            case "oldest" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "createdAt"
                    );

            default ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    );
        };
    }

    // =========================================================
    // RESTAURANT SORT
    // =========================================================

    private Sort getRestaurantSort(
            String sort) {

        if (sort == null) {

            return Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
            );
        }

        return switch (
                sort.toLowerCase()
        ) {

            case "rating" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "rating"
                    );

            case "name" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "name"
                    );

            case "oldest" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "createdAt"
                    );

            default ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    );
        };
    }

    // =========================================================
    // REEL SORT
    // =========================================================

    private Sort getReelSort(
            String sort) {

        if (sort == null) {

            return Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
            );
        }

        return switch (
                sort.toLowerCase()
        ) {

            case "popular" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "viewCount"
                    );

            case "oldest" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "createdAt"
                    );

            default ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    );
        };
    }
}