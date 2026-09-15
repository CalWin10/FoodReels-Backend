package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.CommentRequestDTO;
import com.foodreels.backend.dto.CommentResponseDTO;
import com.foodreels.backend.service.ReelCommentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Comments", description = "Reel comment creation, retrieval and deletion APIs.")
public class ReelCommentController {

    private final ReelCommentService commentService;

    public ReelCommentController(
            ReelCommentService commentService) {

        this.commentService = commentService;
    }

    @Operation(
            summary = "Create a comment",
            description = "Adds a comment to the specified reel for the authenticated user.",
            parameters = {
                    @Parameter(name = "reelId", description = "Reel ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/reels/{reelId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long reelId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CommentRequestDTO requestDTO) {

        String email = jwt.getSubject();

        CommentResponseDTO response =
                commentService.createComment(
                        reelId,
                        email,
                        requestDTO
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get reel comments",
            description = "Returns comments belonging to the specified reel.",
            parameters = {
                    @Parameter(name = "reelId", description = "Reel ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/reels/{reelId}/comments")
    public ResponseEntity<List<CommentResponseDTO>>
            getCommentsByReel(
                    @PathVariable Long reelId) {

        return ResponseEntity.ok(
                commentService.getCommentsByReel(reelId)
        );
    }

    @Operation(
            summary = "Delete a comment",
            description = "Deletes a comment when the authenticated user is its author or an admin.",
            parameters = {
                    @Parameter(name = "commentId", description = "Comment ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        commentService.deleteComment(
                commentId,
                email
        );

        return ResponseEntity.noContent().build();
    }
}