package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.CategoryScoreDTO;
import com.foodreels.backend.dto.ReelScoreDTO;
import com.foodreels.backend.dto.UserBehaviorProfileDTO;
import com.foodreels.backend.service.RecommendationScoringService;
import com.foodreels.backend.service.RecommendationService;
import com.foodreels.backend.service.UserBehaviorProfileService;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations", description = "Personalized FoodReels recommendation and recommendation-debug APIs.")
@SecurityRequirement(name = "bearerAuth")
public class UserBehaviorProfileController {

    private final UserBehaviorProfileService profileService;
    private final RecommendationScoringService scoringService;
    private final RecommendationService recommendationService;

    public UserBehaviorProfileController(
            UserBehaviorProfileService profileService,
            RecommendationScoringService scoringService,
            RecommendationService recommendationService) {

        this.profileService = profileService;
        this.scoringService = scoringService;
        this.recommendationService = recommendationService;
    }

    @Operation(
            summary = "Get my behavior profile",
            description = "Returns the authenticated user preferences and watch, like, save and comment counts by category for recommendation debugging."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserBehaviorProfileDTO> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                profileService.buildProfile(email));
    }

    @Operation(
            summary = "Get my category scores",
            description = "Returns category recommendation scores calculated for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/scores")
    public ResponseEntity<List<CategoryScoreDTO>> getMyRecommendationScores(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                scoringService
                        .calculateCategoryScores(email));
    }

    @Operation(
            summary = "Get scored recommendation candidates",
            description = "Returns candidate reels with recommendation scores for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/reels")
    public ResponseEntity<List<ReelScoreDTO>> getRecommendedReels(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                recommendationService
                        .scoreCandidateReels(email));
    }
}