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

import com.foodreels.backend.dto.PreferenceRequestDTO;
import com.foodreels.backend.dto.PreferenceResponseDTO;
import com.foodreels.backend.service.UserPreferenceService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/preferences")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Preferences", description = "User food category preference APIs.")
public class UserPreferenceController {

    private final UserPreferenceService preferenceService;

    public UserPreferenceController(
            UserPreferenceService preferenceService) {

        this.preferenceService =
                preferenceService;
    }

    @Operation(
            summary = "Add a food preference",
            description = "Adds a food category preference for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
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

    @Operation(
            summary = "Get my food preferences",
            description = "Returns food category preferences for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
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

    @Operation(
            summary = "Remove a food preference",
            description = "Removes the specified food category from the authenticated user preferences.",
            parameters = {
                    @Parameter(name = "category", description = "Food category", example = "Pizza")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
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