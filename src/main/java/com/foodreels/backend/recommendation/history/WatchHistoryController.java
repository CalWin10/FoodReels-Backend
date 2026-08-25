package com.foodreels.backend.recommendation.history;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.recommendation.history.WatchHistoryResponseDTO;
import com.foodreels.backend.recommendation.history.WatchHistoryService;

@RestController
@RequestMapping("/api/watch-history")
public class WatchHistoryController {

    private final WatchHistoryService watchHistoryService;

    public WatchHistoryController(
            WatchHistoryService watchHistoryService) {

        this.watchHistoryService = watchHistoryService;
    }

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

