package com.foodreels.backend.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.CategoryScoreDTO;
import com.foodreels.backend.dto.ReelFeedPageDTO;
import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.dto.ReelScoreDTO;
import com.foodreels.backend.entity.Reel;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.entity.WatchHistory;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.mapper.ReelMapper;
import com.foodreels.backend.repository.ReelRepository;
import com.foodreels.backend.repository.UserRepository;
import com.foodreels.backend.repository.WatchHistoryRepository;

@Service
public class RecommendationService {

    private static final int CANDIDATE_LIMIT = 100;

    private final RecommendationScoringService scoringService;
    private final ReelRepository reelRepository;
    private final ReelMapper reelMapper;
    private final UserRepository userRepository;
    private final WatchHistoryRepository watchHistoryRepository;

    public RecommendationService(
            RecommendationScoringService scoringService,
            ReelRepository reelRepository,
            ReelMapper reelMapper,
            UserRepository userRepository,
            WatchHistoryRepository watchHistoryRepository) {

        this.scoringService = scoringService;
        this.reelRepository = reelRepository;
        this.reelMapper = reelMapper;
        this.userRepository = userRepository;
        this.watchHistoryRepository = watchHistoryRepository;
    }

    // =========================================================
    // DEBUG / DEVELOPMENT REEL SCORING
    // =========================================================

    public List<ReelScoreDTO> scoreCandidateReels(
            String email) {

        Map<String, Integer> categoryScores =
                getCategoryScoreMap(email);

        Map<Long, Integer> watchCounts =
                getWatchCountMap(email);

        Pageable pageable =
                PageRequest.of(
                        0,
                        CANDIDATE_LIMIT,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        List<Reel> candidates =
                reelRepository
                        .findAll(pageable)
                        .getContent();

        return candidates
                .stream()
                .map(reel ->
                        scoreReel(
                                reel,
                                categoryScores,
                                watchCounts
                        )
                )
                .sorted(
                        (a, b) -> {

                            int scoreComparison =
                                    Integer.compare(
                                            b.getTotalScore(),
                                            a.getTotalScore()
                                    );

                            if (scoreComparison != 0) {
                                return scoreComparison;
                            }

                            return Long.compare(
                                    b.getReelId(),
                                    a.getReelId()
                            );
                        }
                )
                .toList();
    }

    // =========================================================
    // PERSONALIZED FEED WITH REDIS CACHE
    // =========================================================

    @Cacheable(
            value = "personalizedFeed",
            key = "#email + ':page:' + #page + ':size:' + #size"
    )
    public ReelFeedPageDTO getPersonalizedFeed(
            String email,
            int page,
            int size) {

        /*
         * If this message appears:
         * Redis did NOT contain the requested personalized feed.
         *
         * If the same request is sent again and this does NOT appear:
         * Redis CACHE HIT.
         */
        System.out.println(
                "PERSONALIZED FEED CALCULATED - CACHE MISS"
        );

        // -----------------------------------------------------
        // Defensive pagination
        // -----------------------------------------------------

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        if (size > 50) {
            size = 50;
        }

        // -----------------------------------------------------
        // User category-interest scores
        // -----------------------------------------------------

        Map<String, Integer> categoryScores =
                getCategoryScoreMap(email);

        // -----------------------------------------------------
        // User watch counts for each Reel
        // -----------------------------------------------------

        Map<Long, Integer> watchCounts =
                getWatchCountMap(email);

        // -----------------------------------------------------
        // Candidate generation
        // Latest 100 reels
        // -----------------------------------------------------

        Pageable candidatePageable =
                PageRequest.of(
                        0,
                        CANDIDATE_LIMIT,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        List<Reel> candidates =
                new ArrayList<>(
                        reelRepository
                                .findAll(candidatePageable)
                                .getContent()
                );

        // -----------------------------------------------------
        // Personalized ranking
        // -----------------------------------------------------

        /*
         * Existing user:
         *
         * categoryScores contains interests,
         * therefore score and rank candidate reels.
         *
         * New user:
         *
         * categoryScores is empty,
         * therefore candidates remain newest-first.
         */
        if (!categoryScores.isEmpty()) {

            candidates.sort(
                    (reelA, reelB) -> {

                        int scoreA =
                                calculateTotalScore(
                                        reelA,
                                        categoryScores,
                                        watchCounts
                                );

                        int scoreB =
                                calculateTotalScore(
                                        reelB,
                                        categoryScores,
                                        watchCounts
                                );

                        // Higher score first
                        int scoreComparison =
                                Integer.compare(
                                        scoreB,
                                        scoreA
                                );

                        if (scoreComparison != 0) {
                            return scoreComparison;
                        }

                        // Same score -> newest Reel first
                        return compareReelsByCreatedAt(
                                reelA,
                                reelB
                        );
                    }
            );
        }

        // -----------------------------------------------------
        // Pagination AFTER ranking
        // -----------------------------------------------------

        int start =
                page * size;

        int totalElements =
                candidates.size();

        int totalPages =
                calculateTotalPages(
                        totalElements,
                        size
                );

        // Requested page is outside available results
        if (start >= candidates.size()) {

            return new ReelFeedPageDTO(
                    List.of(),
                    totalElements,
                    totalPages,
                    page,
                    size,
                    page == 0,
                    true
            );
        }

        int end =
                Math.min(
                        start + size,
                        candidates.size()
                );

        List<ReelResponseDTO> content =
                candidates
                        .subList(
                                start,
                                end
                        )
                        .stream()
                        .map(
                                reelMapper::toResponseDTO
                        )
                        .toList();

        boolean first =
                page == 0;

        boolean last =
                page >= totalPages - 1;

        return new ReelFeedPageDTO(
                content,
                totalElements,
                totalPages,
                page,
                size,
                first,
                last
        );
    }

    // =========================================================
    // TOTAL REEL SCORE
    // =========================================================

    private int calculateTotalScore(
            Reel reel,
            Map<String, Integer> categoryScores,
            Map<Long, Integer> watchCounts) {

        String category =
                normalizeCategory(
                        reel.getFood()
                                .getCategory()
                );

        int categoryScore =
                categoryScores.getOrDefault(
                        category,
                        0
                );

        int recencyScore =
                calculateRecencyScore(
                        reel
                );

        int popularityScore =
                calculatePopularityScore(
                        reel
                );

        int watchPenalty =
                calculateWatchPenalty(
                        reel,
                        watchCounts
                );

        return categoryScore
                + recencyScore
                + popularityScore
                - watchPenalty;
    }

    // =========================================================
    // DEBUG SCORE DETAILS
    // =========================================================

    private ReelScoreDTO scoreReel(
            Reel reel,
            Map<String, Integer> categoryScores,
            Map<Long, Integer> watchCounts) {

        String category =
                normalizeCategory(
                        reel.getFood()
                                .getCategory()
                );

        int categoryScore =
                categoryScores.getOrDefault(
                        category,
                        0
                );

        int recencyScore =
                calculateRecencyScore(
                        reel
                );

        int popularityScore =
                calculatePopularityScore(
                        reel
                );

        int watchPenalty =
                calculateWatchPenalty(
                        reel,
                        watchCounts
                );

        int totalScore =
                categoryScore
                        + recencyScore
                        + popularityScore
                        - watchPenalty;

        ReelScoreDTO dto =
                new ReelScoreDTO();

        dto.setReelId(
                reel.getId()
        );

        dto.setCaption(
                reel.getCaption()
        );

        dto.setFoodName(
                reel.getFood()
                        .getName()
        );

        dto.setCategory(
                category
        );

        dto.setCategoryScore(
                categoryScore
        );

        dto.setRecencyScore(
                recencyScore
        );

        dto.setPopularityScore(
                popularityScore
        );

        dto.setWatchPenalty(
                watchPenalty
        );

        dto.setTotalScore(
                totalScore
        );

        return dto;
    }

    // =========================================================
    // CATEGORY INTEREST SCORE MAP
    // =========================================================

    private Map<String, Integer> getCategoryScoreMap(
            String email) {

        List<CategoryScoreDTO> scores =
                scoringService
                        .calculateCategoryScores(
                                email
                        );

        Map<String, Integer> scoreMap =
                new HashMap<>();

        for (CategoryScoreDTO score : scores) {

            scoreMap.put(
                    normalizeCategory(
                            score.getCategory()
                    ),
                    score.getScore()
            );
        }

        return scoreMap;
    }

    // =========================================================
    // WATCH HISTORY MAP
    // =========================================================

    private Map<Long, Integer> getWatchCountMap(
            String email) {

        User user =
                userRepository
                        .findByEmail(
                                email
                        )
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                "User not found"
                                        )
                        );

        List<WatchHistory> histories =
                watchHistoryRepository
                        .findByUser_IdOrderByLastWatchedAtDesc(
                                user.getId()
                        );

        Map<Long, Integer> watchCounts =
                new HashMap<>();

        for (WatchHistory history : histories) {

            watchCounts.put(
                    history
                            .getReel()
                            .getId(),
                    history
                            .getWatchCount()
            );
        }

        return watchCounts;
    }

    // =========================================================
    // WATCH PENALTY
    // =========================================================

    private int calculateWatchPenalty(
            Reel reel,
            Map<Long, Integer> watchCounts) {

        int watchCount =
                watchCounts.getOrDefault(
                        reel.getId(),
                        0
                );

        if (watchCount >= 5) {
            return 5;
        }

        if (watchCount >= 3) {
            return 3;
        }

        if (watchCount >= 1) {
            return 1;
        }

        return 0;
    }

    // =========================================================
    // RECENCY SCORE
    // =========================================================

    private int calculateRecencyScore(
            Reel reel) {

        if (reel.getCreatedAt() == null) {
            return 0;
        }

        long daysOld =
                ChronoUnit.DAYS.between(
                        reel.getCreatedAt(),
                        LocalDateTime.now()
                );

        if (daysOld <= 1) {
            return 5;
        }

        if (daysOld <= 7) {
            return 3;
        }

        if (daysOld <= 30) {
            return 1;
        }

        return 0;
    }

    // =========================================================
    // POPULARITY SCORE
    // =========================================================

    private int calculatePopularityScore(
            Reel reel) {

        Long views =
                reel.getViewCount();

        if (views == null) {
            return 0;
        }

        if (views >= 100) {
            return 5;
        }

        if (views >= 20) {
            return 3;
        }

        if (views >= 5) {
            return 2;
        }

        if (views >= 1) {
            return 1;
        }

        return 0;
    }

    // =========================================================
    // TOTAL PAGE CALCULATION
    // =========================================================

    private int calculateTotalPages(
            int totalElements,
            int size) {

        if (totalElements == 0) {
            return 0;
        }

        return (int) Math.ceil(
                (double) totalElements
                        / size
        );
    }

    // =========================================================
    // DETERMINISTIC TIE BREAK
    // =========================================================

    private int compareReelsByCreatedAt(
            Reel reelA,
            Reel reelB) {

        LocalDateTime createdA =
                reelA.getCreatedAt();

        LocalDateTime createdB =
                reelB.getCreatedAt();

        if (createdA == null
                && createdB == null) {

            return Long.compare(
                    reelB.getId(),
                    reelA.getId()
            );
        }

        if (createdA == null) {
            return 1;
        }

        if (createdB == null) {
            return -1;
        }

        int dateComparison =
                createdB.compareTo(
                        createdA
                );

        if (dateComparison != 0) {
            return dateComparison;
        }

        // Same timestamp -> higher Reel ID first
        return Long.compare(
                reelB.getId(),
                reelA.getId()
        );
    }

    // =========================================================
    // NORMALIZE CATEGORY
    // =========================================================

    private String normalizeCategory(
            String category) {

        if (category == null) {
            return "UNKNOWN";
        }

        return category
                .trim()
                .toUpperCase();
    }
}