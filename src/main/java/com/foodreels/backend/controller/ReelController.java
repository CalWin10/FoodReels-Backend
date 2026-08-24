package com.foodreels.backend.controller;

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

import com.foodreels.backend.dto.ReelRequestDTO;
import com.foodreels.backend.dto.ReelResponseDTO;
import com.foodreels.backend.service.ReelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reels")
public class ReelController {

        private final ReelService reelService;

        public ReelController(ReelService reelService) {
                this.reelService = reelService;
        }

        // Create reel
        @PostMapping
        public ResponseEntity<ReelResponseDTO> createReel(
                        @Valid @RequestBody ReelRequestDTO requestDTO) {

                ReelResponseDTO createdReel = reelService.createReel(requestDTO);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(createdReel);
        }

        // Get all reels
        @GetMapping
        public ResponseEntity<List<ReelResponseDTO>> getAllReels() {

                List<ReelResponseDTO> reels = reelService.getAllReels();

                return ResponseEntity.ok(reels);
        }

        // Get reel by ID
        @GetMapping("/{id}")
        public ResponseEntity<ReelResponseDTO> getReelById(
                        @PathVariable Long id) {

                ReelResponseDTO reel = reelService.getReelById(id);

                return ResponseEntity.ok(reel);
        }

        // Get reels belonging to a food
        @GetMapping("/food/{foodId}")
        public ResponseEntity<List<ReelResponseDTO>> getReelsByFood(
                        @PathVariable Long foodId) {

                List<ReelResponseDTO> reels = reelService.getReelsByFoodId(foodId);

                return ResponseEntity.ok(reels);
        }

        // Update reel
        @PutMapping("/{id}")
        public ResponseEntity<ReelResponseDTO> updateReel(
                        @PathVariable Long id,
                        @Valid @RequestBody ReelRequestDTO requestDTO) {

                ReelResponseDTO updatedReel = reelService.updateReel(id, requestDTO);

                return ResponseEntity.ok(updatedReel);
        }

        // Delete reel
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteReel(
                        @PathVariable Long id) {

                reelService.deleteReel(id);

                return ResponseEntity.noContent().build();
        }

        @GetMapping("/feed")
        public ResponseEntity<Page<ReelResponseDTO>> getReelFeed(

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                return ResponseEntity.ok(
                                reelService.getReelFeed(page, size));
        }

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
}