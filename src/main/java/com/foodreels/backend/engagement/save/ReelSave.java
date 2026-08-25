package com.foodreels.backend.engagement.save;

import com.foodreels.backend.reel.Reel;

import com.foodreels.backend.user.User;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "reel_saves",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "reel_id"}
        )
    }
)
public class ReelSave {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reel_id", nullable = false)
    private Reel reel;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}



