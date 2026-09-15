package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Saves", description = "Saved reel management APIs.")
public class ReelSaveController {

    private final ReelSaveService reelSaveService;

    public ReelSaveController(
            ReelSaveService reelSaveService) {

        this.reelSaveService = reelSaveService;
    }

    @Operation(
            summary = "Save a reel",
            description = "Saves the specified reel for the authenticated user.",
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

    @Operation(
            summary = "Unsave a reel",
            description = "Removes the specified reel from the authenticated user saved reels.",
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

    @Operation(
            summary = "Get my save status",
            description = "Returns whether the authenticated user has saved the specified reel.",
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

    @Operation(
            summary = "Get my saved reels",
            description = "Returns reels saved by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/saves")
    public ResponseEntity<List<ReelResponseDTO>> getSavedReels(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                reelSaveService.getSavedReels(email)
        );
    }
}