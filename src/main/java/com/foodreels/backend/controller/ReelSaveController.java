package com.foodreels.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.dto.SaveResponseDTO;
import com.foodreels.backend.service.ReelSaveService;

@RestController
@RequestMapping("/api")
public class ReelSaveController {

    private final ReelSaveService reelSaveService;

    public ReelSaveController(
            ReelSaveService reelSaveService) {

        this.reelSaveService = reelSaveService;
    }

    @PostMapping("/reels/{reelId}/saves")
    public ResponseEntity<SaveResponseDTO> saveReel(
            @PathVariable Long reelId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                reelSaveService.saveReel(
                        reelId,
                        email
                )
        );
    }

    @DeleteMapping("/reels/{reelId}/saves")
    public ResponseEntity<SaveResponseDTO> unsaveReel(
            @PathVariable Long reelId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                reelSaveService.unsaveReel(
                        reelId,
                        email
                )
        );
    }

    @GetMapping("/reels/{reelId}/saves/status")
    public ResponseEntity<SaveResponseDTO> getSaveStatus(
            @PathVariable Long reelId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                reelSaveService.getSaveStatus(
                        reelId,
                        email
                )
        );
    }

    @GetMapping("/saves")
    public ResponseEntity<List<ReelResponseDTO>> getSavedReels(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                reelSaveService.getSavedReels(email)
        );
    }
}