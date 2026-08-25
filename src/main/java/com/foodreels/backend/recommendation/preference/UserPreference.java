package com.foodreels.backend.recommendation.preference;

import com.foodreels.backend.user.User;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
    name = "user_preferences",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "category"}
        )
    }
)
public class UserPreference {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    private User user;

    @Column(
        nullable = false,
        length = 100
    )
    private String category;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}


