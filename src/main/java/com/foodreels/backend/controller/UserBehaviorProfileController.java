package com.foodreels.backend.controller;

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

    @GetMapping("/profile")
    public ResponseEntity<UserBehaviorProfileDTO> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                profileService.buildProfile(email));
    }

    @GetMapping("/scores")
    public ResponseEntity<List<CategoryScoreDTO>> getMyRecommendationScores(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                scoringService
                        .calculateCategoryScores(email));
    }

    @GetMapping("/reels")
    public ResponseEntity<List<ReelScoreDTO>> getRecommendedReels(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                recommendationService
                        .scoreCandidateReels(email));
    }
}