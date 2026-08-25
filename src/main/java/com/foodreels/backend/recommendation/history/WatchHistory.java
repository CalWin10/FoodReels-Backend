package com.foodreels.backend.recommendation.history;

import com.foodreels.backend.reel.Reel;

import com.foodreels.backend.user.User;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "watch_history",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "reel_id"}
        )
    }
)
public class WatchHistory {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reel_id", nullable = false)
    private Reel reel;

    private Integer watchCount;

    private LocalDateTime firstWatchedAt;

    private LocalDateTime lastWatchedAt;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Reel getReel() {
        return reel;
    }

    public void setReel(Reel reel) {
        this.reel = reel;
    }

    public Integer getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    public LocalDateTime getFirstWatchedAt() {
        return firstWatchedAt;
    }

    public void setFirstWatchedAt(LocalDateTime firstWatchedAt) {
        this.firstWatchedAt = firstWatchedAt;
    }

    public LocalDateTime getLastWatchedAt() {
        return lastWatchedAt;
    }

    public void setLastWatchedAt(LocalDateTime lastWatchedAt) {
        this.lastWatchedAt = lastWatchedAt;
    }
}



