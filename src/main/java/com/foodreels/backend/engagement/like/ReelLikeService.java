package com.foodreels.backend.engagement.like;

import org.springframework.stereotype.Service;

import com.foodreels.backend.engagement.like.LikeResponseDTO;
import com.foodreels.backend.reel.Reel;
import com.foodreels.backend.user.User;
import com.foodreels.backend.exception.ReelNotFoundException;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.engagement.like.ReelLikeRepository;
import com.foodreels.backend.reel.ReelRepository;
import com.foodreels.backend.user.UserRepository;

@Service
public class ReelLikeService {

    private final ReelLikeRepository reelLikeRepository;
    private final ReelRepository reelRepository;
    private final UserRepository userRepository;

    public ReelLikeService(
            ReelLikeRepository reelLikeRepository,
            ReelRepository reelRepository,
            UserRepository userRepository) {

        this.reelLikeRepository = reelLikeRepository;
        this.reelRepository = reelRepository;
        this.userRepository = userRepository;
    }

    // Like a reel
    public LikeResponseDTO likeReel(
            Long reelId,
            String email) {

        User user = findUserByEmail(email);
        Reel reel = findReelById(reelId);

        boolean alreadyLiked =
                reelLikeRepository.existsByUser_IdAndReel_Id(
                        user.getId(),
                        reelId
                );

        // Keep POST idempotent
        if (!alreadyLiked) {

            ReelLike like = new ReelLike();

            like.setUser(user);
            like.setReel(reel);

            reelLikeRepository.save(like);
        }

        long likeCount =
                reelLikeRepository.countByReel_Id(reelId);

        return new LikeResponseDTO(
                true,
                likeCount
        );
    }

    // Unlike a reel
    public LikeResponseDTO unlikeReel(
            Long reelId,
            String email) {

        User user = findUserByEmail(email);

        // Also verifies that reel exists
        findReelById(reelId);

        reelLikeRepository
                .findByUser_IdAndReel_Id(
                        user.getId(),
                        reelId
                )
                .ifPresent(reelLikeRepository::delete);

        long likeCount =
                reelLikeRepository.countByReel_Id(reelId);

        return new LikeResponseDTO(
                false,
                likeCount
        );
    }

    // Check current user's like status
    public LikeResponseDTO getLikeStatus(
            Long reelId,
            String email) {

        User user = findUserByEmail(email);

        findReelById(reelId);

        boolean liked =
                reelLikeRepository.existsByUser_IdAndReel_Id(
                        user.getId(),
                        reelId
                );

        long likeCount =
                reelLikeRepository.countByReel_Id(reelId);

        return new LikeResponseDTO(
                liked,
                likeCount
        );
    }

    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));
    }

    private Reel findReelById(Long reelId) {

        return reelRepository.findById(reelId)
                .orElseThrow(() ->
                        new ReelNotFoundException(
                                "Reel not found with id: " + reelId
                        ));
    }
}

