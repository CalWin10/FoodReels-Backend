package com.foodreels.backend.engagement.like;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.engagement.like.LikeResponseDTO;
import com.foodreels.backend.engagement.like.ReelLikeService;

@RestController
@RequestMapping("/api/reels")
public class ReelLikeController {

    private final ReelLikeService reelLikeService;

    public ReelLikeController(
            ReelLikeService reelLikeService) {

        this.reelLikeService = reelLikeService;
    }

    // Like reel
    @PostMapping("/{reelId}/likes")
    public ResponseEntity<LikeResponseDTO> likeReel(
            @PathVariable Long reelId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        LikeResponseDTO response =
                reelLikeService.likeReel(
                        reelId,
                        email
                );

        return ResponseEntity.ok(response);
    }

    // Unlike reel
    @DeleteMapping("/{reelId}/likes")
    public ResponseEntity<LikeResponseDTO> unlikeReel(
            @PathVariable Long reelId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        LikeResponseDTO response =
                reelLikeService.unlikeReel(
                        reelId,
                        email
                );

        return ResponseEntity.ok(response);
    }

    // Current user's like status
    @GetMapping("/{reelId}/likes/status")
    public ResponseEntity<LikeResponseDTO> getLikeStatus(
            @PathVariable Long reelId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        LikeResponseDTO response =
                reelLikeService.getLikeStatus(
                        reelId,
                        email
                );

        return ResponseEntity.ok(response);
    }
}

