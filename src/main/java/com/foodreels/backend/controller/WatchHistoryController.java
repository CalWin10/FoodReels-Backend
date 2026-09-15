package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.WatchHistoryResponseDTO;
import com.foodreels.backend.service.WatchHistoryService;

@RestController
@RequestMapping("/api/watch-history")
@Tag(name = "Watch History", description = "User reel watch history APIs.")
@SecurityRequirement(name = "bearerAuth")
public class WatchHistoryController {

    private final WatchHistoryService watchHistoryService;

    public WatchHistoryController(
            WatchHistoryService watchHistoryService) {

        this.watchHistoryService = watchHistoryService;
    }

    @Operation(
            summary = "Get my watch history",
            description = "Returns reel watch history for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<List<WatchHistoryResponseDTO>>
            getMyWatchHistory(
                    @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        return ResponseEntity.ok(
                watchHistoryService
                        .getMyWatchHistory(email)
        );
    }
}