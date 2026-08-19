package com.foodreels.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.UserRequestDTO;
import com.foodreels.backend.dto.UserResponseDTO;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.exception.UserNotFoundException;
import com.foodreels.backend.mapper.UserMapper;
import com.foodreels.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // Create user
    public UserResponseDTO createUser(
            UserRequestDTO userRequestDTO) {

        User user = userMapper.toEntity(userRequestDTO);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDTO(savedUser);
    }

    // Get all users
    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    // Get user by id
    public UserResponseDTO getUserById(Long id) {

        User user = findUserById(id);

        return userMapper.toResponseDTO(user);
    }

    // Update user
    public UserResponseDTO updateUser(
            Long id,
            UserRequestDTO userRequestDTO) {

        User existingUser = findUserById(id);

        existingUser.setName(userRequestDTO.getName());
        existingUser.setEmail(userRequestDTO.getEmail());
        existingUser.setPassword(userRequestDTO.getPassword());
        existingUser.setProfileImageUrl(
                userRequestDTO.getProfileImageUrl());
        existingUser.setRole(userRequestDTO.getRole());

        User updatedUser = userRepository.save(existingUser);

        return userMapper.toResponseDTO(updatedUser);
    }

    // Delete user
    public void deleteUser(Long id) {

        User existingUser = findUserById(id);

        userRepository.delete(existingUser);
    }

    // Internal method used by this service
    private User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id));
    }
}