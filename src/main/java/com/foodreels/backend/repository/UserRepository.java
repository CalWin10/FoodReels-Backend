package com.foodreels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodreels.backend.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
    
}
