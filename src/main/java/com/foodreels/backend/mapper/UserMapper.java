package com.foodreels.backend.mapper;

import org.springframework.stereotype.Component;

import com.foodreels.backend.dto.UserRequestDTO;
import com.foodreels.backend.dto.UserResponseDTO;
import com.foodreels.backend.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto){
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        user.setRole(dto.getRole());

        return user;
    }

    public UserResponseDTO toResponseDTO(User user){
        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());


        return dto;
    }
}
