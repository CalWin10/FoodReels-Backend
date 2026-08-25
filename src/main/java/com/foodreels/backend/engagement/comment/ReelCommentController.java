package com.foodreels.backend.engagement.comment;

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

import com.foodreels.backend.engagement.comment.CommentRequestDTO;
import com.foodreels.backend.engagement.comment.CommentResponseDTO;
import com.foodreels.backend.engagement.comment.ReelCommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ReelCommentController {

    private final ReelCommentService commentService;

    public ReelCommentController(
            ReelCommentService commentService) {

        this.commentService = commentService;
    }

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

    @GetMapping("/reels/{reelId}/comments")
    public ResponseEntity<List<CommentResponseDTO>>
            getCommentsByReel(
                    @PathVariable Long reelId) {

        return ResponseEntity.ok(
                commentService.getCommentsByReel(reelId)
        );
    }

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

