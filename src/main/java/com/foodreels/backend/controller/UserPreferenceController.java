package com.foodreels.backend.controller;

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

import com.foodreels.backend.dto.PreferenceRequestDTO;
import com.foodreels.backend.dto.PreferenceResponseDTO;
import com.foodreels.backend.service.UserPreferenceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferenceController {

    private final UserPreferenceService preferenceService;

    public UserPreferenceController(
            UserPreferenceService preferenceService) {

        this.preferenceService =
                preferenceService;
    }

    @PostMapping
    public ResponseEntity<PreferenceResponseDTO>
            addPreference(
                    @AuthenticationPrincipal Jwt jwt,
                    @Valid
                    @RequestBody
                    PreferenceRequestDTO requestDTO) {

        String email =
                jwt.getSubject();

        PreferenceResponseDTO response =
                preferenceService.addPreference(
                        email,
                        requestDTO
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PreferenceResponseDTO>>
            getMyPreferences(
                    @AuthenticationPrincipal Jwt jwt) {

        String email =
                jwt.getSubject();

        return ResponseEntity.ok(
                preferenceService
                        .getMyPreferences(email)
        );
    }

    @DeleteMapping("/{category}")
    public ResponseEntity<Void>
            removePreference(
                    @PathVariable String category,
                    @AuthenticationPrincipal Jwt jwt) {

        String email =
                jwt.getSubject();

        preferenceService.removePreference(
                email,
                category
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}