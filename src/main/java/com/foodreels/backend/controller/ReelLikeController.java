package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.LikeResponseDTO;
import com.foodreels.backend.service.ReelLikeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/reels")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Likes", description = "Reel like and unlike APIs.")
public class ReelLikeController {

    private final ReelLikeService reelLikeService;

    public ReelLikeController(
            ReelLikeService reelLikeService) {

        this.reelLikeService = reelLikeService;
    }

    // Like reel
    @Operation(
            summary = "Like a reel",
            description = "Likes the specified reel for the authenticated user and returns its like status and count.",
            parameters = {
                    @Parameter(name = "reelId", description = "Reel ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
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
    @Operation(
            summary = "Unlike a reel",
            description = "Removes the authenticated user like from the specified reel and returns its like status and count.",
            parameters = {
                    @Parameter(name = "reelId", description = "Reel ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
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
    @Operation(
            summary = "Get my like status",
            description = "Returns whether the authenticated user likes the specified reel and its like count.",
            parameters = {
                    @Parameter(name = "reelId", description = "Reel ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
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