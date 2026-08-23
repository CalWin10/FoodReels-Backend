package com.foodreels.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodreels.backend.dto.LoginRequestDTO;
import com.foodreels.backend.dto.LoginResponseDTO;
import com.foodreels.backend.dto.RegisterRequestDTO;
import com.foodreels.backend.dto.UserResponseDTO;
import com.foodreels.backend.entity.User;
import com.foodreels.backend.entity.UserRole;
import com.foodreels.backend.exception.DuplicateEmailException;
import com.foodreels.backend.exception.InvalidCredentialsException;
import com.foodreels.backend.mapper.UserMapper;
import com.foodreels.backend.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    // -------------------------
    // REGISTER
    // -------------------------
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();

        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setProfileImageUrl(registerRequestDTO.getProfileImageUrl());

        // Hash the password before storing it
        user.setPassword(
                passwordEncoder.encode(registerRequestDTO.getPassword())
        );

        // User cannot choose their own role
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDTO(savedUser);
    }

    // -------------------------
    // LOGIN
    // -------------------------
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        // Find user by email
        User user = userRepository
                .findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        // Compare raw password with BCrypt hash in database
        boolean isPasswordValid = passwordEncoder.matches(
                loginRequestDTO.getPassword(),
                user.getPassword()
        );

        if (!isPasswordValid) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT after successful authentication
        String token = jwtService.generateToken(user);

        // Build login response
        LoginResponseDTO response = new LoginResponseDTO();

        response.setToken(token);
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }
}