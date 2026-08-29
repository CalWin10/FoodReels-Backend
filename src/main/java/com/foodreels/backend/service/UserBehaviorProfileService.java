package com.foodreels.backend.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.UserBehaviorProfileDTO;
import com.foodreels.backend.entity.ReelComment;
import com.foodreels.backend.entity.ReelLike;
import com.foodreels.backend.entity.ReelSave;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.entity.UserPreference;
import com.foodreels.backend.entity.WatchHistory;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.repository.ReelCommentRepository;
import com.foodreels.backend.repository.ReelLikeRepository;
import com.foodreels.backend.repository.ReelSaveRepository;
import com.foodreels.backend.repository.UserPreferenceRepository;
import com.foodreels.backend.repository.UserRepository;
import com.foodreels.backend.repository.WatchHistoryRepository;

@Service
public class UserBehaviorProfileService {

    private final UserRepository userRepository;

    private final UserPreferenceRepository preferenceRepository;

    private final WatchHistoryRepository watchHistoryRepository;

    private final ReelLikeRepository likeRepository;

    private final ReelSaveRepository saveRepository;

    private final ReelCommentRepository commentRepository;

    public UserBehaviorProfileService(
            UserRepository userRepository,
            UserPreferenceRepository preferenceRepository,
            WatchHistoryRepository watchHistoryRepository,
            ReelLikeRepository likeRepository,
            ReelSaveRepository saveRepository,
            ReelCommentRepository commentRepository) {

        this.userRepository = userRepository;

        this.preferenceRepository = preferenceRepository;

        this.watchHistoryRepository = watchHistoryRepository;

        this.likeRepository = likeRepository;

        this.saveRepository = saveRepository;

        this.commentRepository = commentRepository;
    }

    public UserBehaviorProfileDTO buildProfile(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        UserBehaviorProfileDTO profile =
                new UserBehaviorProfileDTO();

        profile.setUserId(user.getId());

        profile.setPreferences(
                buildPreferences(user.getId())
        );

        profile.setWatchCategoryCounts(
                buildWatchCategoryCounts(user.getId())
        );

        profile.setLikeCategoryCounts(
                buildLikeCategoryCounts(user.getId())
        );

        profile.setSaveCategoryCounts(
                buildSaveCategoryCounts(user.getId())
        );

        profile.setCommentCategoryCounts(
                buildCommentCategoryCounts(user.getId())
        );

        return profile;
    }

    private Set<String> buildPreferences(
            Long userId) {

        List<UserPreference> preferences =
                preferenceRepository
                        .findByUser_IdOrderByCreatedAtAsc(
                                userId
                        );

        Set<String> categories =
                new HashSet<>();

        for (UserPreference preference : preferences) {

            categories.add(
                    normalizeCategory(
                            preference.getCategory()
                    )
            );
        }

        return categories;
    }

    private Map<String, Integer>
            buildWatchCategoryCounts(
                    Long userId) {

        List<WatchHistory> histories =
                watchHistoryRepository
                        .findByUser_IdOrderByLastWatchedAtDesc(
                                userId
                        );

        Map<String, Integer> counts =
                new HashMap<>();

        for (WatchHistory history : histories) {

            String category =
                    normalizeCategory(
                            history.getReel()
                                    .getFood()
                                    .getCategory()
                    );

            counts.merge(
                    category,
                    history.getWatchCount(),
                    Integer::sum
            );
        }

        return counts;
    }

    private Map<String, Integer>
            buildLikeCategoryCounts(
                    Long userId) {

        List<ReelLike> likes =
                likeRepository
                        .findByUser_Id(userId);

        Map<String, Integer> counts =
                new HashMap<>();

        for (ReelLike like : likes) {

            String category =
                    normalizeCategory(
                            like.getReel()
                                    .getFood()
                                    .getCategory()
                    );

            counts.merge(
                    category,
                    1,
                    Integer::sum
            );
        }

        return counts;
    }

    private Map<String, Integer>
            buildSaveCategoryCounts(
                    Long userId) {

        List<ReelSave> saves =
                saveRepository
                        .findByUser_IdOrderByCreatedAtDesc(
                                userId
                        );

        Map<String, Integer> counts =
                new HashMap<>();

        for (ReelSave save : saves) {

            String category =
                    normalizeCategory(
                            save.getReel()
                                    .getFood()
                                    .getCategory()
                    );

            counts.merge(
                    category,
                    1,
                    Integer::sum
            );
        }

        return counts;
    }

    private Map<String, Integer>
            buildCommentCategoryCounts(
                    Long userId) {

        List<ReelComment> comments =
                commentRepository
                        .findByUser_Id(userId);

        Map<String, Integer> counts =
                new HashMap<>();

        for (ReelComment comment : comments) {

            String category =
                    normalizeCategory(
                            comment.getReel()
                                    .getFood()
                                    .getCategory()
                    );

            counts.merge(
                    category,
                    1,
                    Integer::sum
            );
        }

        return counts;
    }

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