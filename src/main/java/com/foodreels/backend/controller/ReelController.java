package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.foodreels.backend.dto.ViewResponseDTO;
import com.foodreels.backend.dto.ReelFeedPageDTO;
import com.foodreels.backend.dto.ReelRequestDTO;
import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.service.RecommendationService;
import com.foodreels.backend.service.ReelService;
import com.foodreels.backend.service.WatchHistoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reels")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reels", description = "Food reel creation, feed, discovery and management APIs.")
public class ReelController {

        private final ReelService reelService;
        private final WatchHistoryService watchHistoryService;
        private final RecommendationService recommendationService;

        public ReelController(
                        ReelService reelService,
                        WatchHistoryService watchHistoryService,
                        RecommendationService recommendationService) {

                this.reelService = reelService;

                this.watchHistoryService = watchHistoryService;

                this.recommendationService = recommendationService;
        }

        // Create reel
        @Operation(
                summary = "Create a reel",
                description = "Creates a reel linked to a food item. Requires the RESTAURANT_OWNER or ADMIN role."
        )
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Resource created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @PostMapping
        public ResponseEntity<ReelResponseDTO> createReel(
                        @Valid @RequestBody ReelRequestDTO requestDTO) {

                ReelResponseDTO createdReel = reelService.createReel(requestDTO);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(createdReel);
        }

        // Get all reels
        @Operation(
                summary = "Get all reels",
                description = "Returns all food reels."
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Request completed successfully"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @GetMapping
        public ResponseEntity<List<ReelResponseDTO>> getAllReels() {

                List<ReelResponseDTO> reels = reelService.getAllReels();

                return ResponseEntity.ok(reels);
        }

        // Get reel by ID
        @Operation(
                summary = "Get a reel",
                description = "Returns the reel identified by its ID.",
                parameters = {
                        @Parameter(name = "id", description = "Reel ID", example = "1")
                }
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Request completed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ReelResponseDTO> getReelById(
                        @PathVariable Long id) {

                ReelResponseDTO reel = reelService.getReelById(id);

                return ResponseEntity.ok(reel);
        }

        // Get reels belonging to a food
        @Operation(
                summary = "Get reels for a food item",
                description = "Returns reels belonging to the specified food item.",
                parameters = {
                        @Parameter(name = "foodId", description = "Food item ID", example = "1")
                }
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Request completed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @GetMapping("/food/{foodId}")
        public ResponseEntity<List<ReelResponseDTO>> getReelsByFood(
                        @PathVariable Long foodId) {

                List<ReelResponseDTO> reels = reelService.getReelsByFoodId(foodId);

                return ResponseEntity.ok(reels);
        }

        // Update reel
        @Operation(
                summary = "Update a reel",
                description = "Updates the specified reel using the supplied details. Requires the RESTAURANT_OWNER or ADMIN role.",
                parameters = {
                        @Parameter(name = "id", description = "Reel ID", example = "1")
                }
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Request completed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @PutMapping("/{id}")
        public ResponseEntity<ReelResponseDTO> updateReel(
                        @PathVariable Long id,
                        @Valid @RequestBody ReelRequestDTO requestDTO) {

                ReelResponseDTO updatedReel = reelService.updateReel(id, requestDTO);

                return ResponseEntity.ok(updatedReel);
        }

        // Delete reel
        @Operation(
                summary = "Delete a reel",
                description = "Deletes the specified reel. Requires the RESTAURANT_OWNER or ADMIN role.",
                parameters = {
                        @Parameter(name = "id", description = "Reel ID", example = "1")
                }
        )
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteReel(
                        @PathVariable Long id) {

                reelService.deleteReel(id);

                return ResponseEntity.noContent().build();
        }

        @Operation(
                summary = "Get the reel feed",
                description = "Returns a paginated reel feed ordered by creation time, newest first.",
                parameters = {
                        @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                        @Parameter(name = "size", description = "Number of results per page", example = "10")
                }
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Request completed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @GetMapping("/feed")
        public ResponseEntity<ReelFeedPageDTO> getReelFeed(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                return ResponseEntity.ok(
                                reelService.getReelFeed(
                                                page,
                                                size));
        }

        @Operation(
                summary = "Discover reels",
                description = "Returns paginated reels with optional search text, restaurant, food and category filters.",
                parameters = {
                        @Parameter(name = "q", description = "Search text", example = "pizza"),
                        @Parameter(name = "restaurantId", description = "Restaurant ID", example = "1"),
                        @Parameter(name = "foodId", description = "Food item ID", example = "1"),
                        @Parameter(name = "category", description = "Food category", example = "Pizza"),
                        @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                        @Parameter(name = "size", description = "Number of results per page", example = "10")
                }
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Request completed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @GetMapping("/discover")
        public ResponseEntity<Page<ReelResponseDTO>> discoverReels(

                        @RequestParam(required = false) String q,

                        @RequestParam(required = false) Long restaurantId,

                        @RequestParam(required = false) Long foodId,

                        @RequestParam(required = false) String category,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                return ResponseEntity.ok(
                                reelService.discoverReels(
                                                q,
                                                restaurantId,
                                                foodId,
                                                category,
                                                page,
                                                size));
        }

        @Operation(
                summary = "Record a reel view",
                description = "Increments the reel view count and records the view in the authenticated user watch history.",
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
        @PostMapping("/{reelId}/view")
        public ResponseEntity<ViewResponseDTO> incrementViewCount(
                        @PathVariable Long reelId,
                        @AuthenticationPrincipal Jwt jwt) {

                String email = jwt.getSubject();

                ViewResponseDTO response = reelService.incrementViewCount(reelId);

                watchHistoryService.recordWatch(
                                reelId,
                                email);

                return ResponseEntity.ok(response);
        }

        @Operation(
                summary = "Get a personalized reel feed",
                description = "Returns a paginated reel feed personalized for the authenticated user.",
                parameters = {
                        @Parameter(name = "page", description = "Zero-based page number", example = "0"),
                        @Parameter(name = "size", description = "Number of results per page", example = "10")
                }
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Request completed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                @ApiResponse(responseCode = "401", description = "Authentication required"),
                @ApiResponse(responseCode = "403", description = "Access denied")
        })
        @GetMapping("/personalized")
        public ResponseEntity<ReelFeedPageDTO> getPersonalizedFeed(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @AuthenticationPrincipal Jwt jwt) {

                String email = jwt.getSubject();

                return ResponseEntity.ok(
                                recommendationService.getPersonalizedFeed(
                                                email,
                                                page,
                                                size));
        }
}