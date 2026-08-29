package com.foodreels.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.CommentRequestDTO;
import com.foodreels.backend.dto.CommentResponseDTO;
import com.foodreels.backend.entity.Reel;
import com.foodreels.backend.entity.ReelComment;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.entity.UserRole;
import com.foodreels.backend.exception.ReelNotFoundException;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.repository.ReelCommentRepository;
import com.foodreels.backend.repository.ReelRepository;
import com.foodreels.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class ReelCommentService {

    private final ReelCommentRepository commentRepository;
    private final ReelRepository reelRepository;
    private final UserRepository userRepository;

    public ReelCommentService(
            ReelCommentRepository commentRepository,
            ReelRepository reelRepository,
            UserRepository userRepository) {

        this.commentRepository = commentRepository;
        this.reelRepository = reelRepository;
        this.userRepository = userRepository;
    }

    @CacheEvict(
        value = "personalizedFeed",
        allEntries = true
)
    public CommentResponseDTO createComment(
            Long reelId,
            String email,
            CommentRequestDTO requestDTO) {

        User user = findUserByEmail(email);
        Reel reel = findReelById(reelId);

        ReelComment comment = new ReelComment();

        comment.setUser(user);
        comment.setReel(reel);
        comment.setContent(requestDTO.getContent());

        ReelComment savedComment =
                commentRepository.save(comment);

        return toResponseDTO(savedComment);
    }

    public List<CommentResponseDTO> getCommentsByReel(
            Long reelId) {

        findReelById(reelId);

        return commentRepository
                .findByReel_IdOrderByCreatedAtDesc(reelId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @CacheEvict(
        value = "personalizedFeed",
        allEntries = true
)
    public void deleteComment(
            Long commentId,
            String email) {

        User user = findUserByEmail(email);

        ReelComment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found"
                                ));

        boolean isOwner =
                comment.getUser()
                        .getId()
                        .equals(user.getId());

        boolean isAdmin =
                user.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException(
                    "You cannot delete this comment"
            );
        }

        commentRepository.delete(comment);
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

    private CommentResponseDTO toResponseDTO(
            ReelComment comment) {

        CommentResponseDTO response =
                new CommentResponseDTO();

        response.setId(comment.getId());
        response.setContent(comment.getContent());

        response.setUserId(
                comment.getUser().getId()
        );

        response.setUserName(
                comment.getUser().getName()
        );

        response.setReelId(
                comment.getReel().getId()
        );

        response.setCreatedAt(
                comment.getCreatedAt()
        );

        return response;
    }
}