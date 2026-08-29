package com.foodreels.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.CategoryScoreDTO;
import com.foodreels.backend.dto.UserBehaviorProfileDTO;

@Service
public class RecommendationScoringService {

    private static final int PREFERENCE_WEIGHT = 6;
    private static final int WATCH_WEIGHT = 1;
    private static final int LIKE_WEIGHT = 3;
    private static final int COMMENT_WEIGHT = 4;
    private static final int SAVE_WEIGHT = 5;

    private final UserBehaviorProfileService profileService;

    public RecommendationScoringService(
            UserBehaviorProfileService profileService) {

        this.profileService = profileService;
    }

    public List<CategoryScoreDTO> calculateCategoryScores(
            String email) {

        UserBehaviorProfileDTO profile =
                profileService.buildProfile(email);

        Map<String, Integer> scores =
                new HashMap<>();

        addPreferenceScores(
                scores,
                profile
        );

        addBehaviorScores(
                scores,
                profile.getWatchCategoryCounts(),
                WATCH_WEIGHT
        );

        addBehaviorScores(
                scores,
                profile.getLikeCategoryCounts(),
                LIKE_WEIGHT
        );

        addBehaviorScores(
                scores,
                profile.getCommentCategoryCounts(),
                COMMENT_WEIGHT
        );

        addBehaviorScores(
                scores,
                profile.getSaveCategoryCounts(),
                SAVE_WEIGHT
        );

        List<CategoryScoreDTO> result =
                new ArrayList<>();

        for (Map.Entry<String, Integer> entry
                : scores.entrySet()) {

            result.add(
                    new CategoryScoreDTO(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        result.sort(
                (a, b) ->
                        Integer.compare(
                                b.getScore(),
                                a.getScore()
                        )
        );

        return result;
    }

    private void addPreferenceScores(
            Map<String, Integer> scores,
            UserBehaviorProfileDTO profile) {

        for (String category
                : profile.getPreferences()) {

            scores.merge(
                    category,
                    PREFERENCE_WEIGHT,
                    Integer::sum
            );
        }
    }

    private void addBehaviorScores(
            Map<String, Integer> scores,
            Map<String, Integer> behaviorCounts,
            int weight) {

        for (Map.Entry<String, Integer> entry
                : behaviorCounts.entrySet()) {

            String category =
                    entry.getKey();

            int count =
                    entry.getValue();

            int weightedScore =
                    count * weight;

            scores.merge(
                    category,
                    weightedScore,
                    Integer::sum
            );
        }
    }
}