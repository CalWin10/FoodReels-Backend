package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.LoginRequestDTO;
import com.foodreels.backend.dto.LoginResponseDTO;
import com.foodreels.backend.dto.RegisterRequestDTO;
import com.foodreels.backend.dto.UserResponseDTO;
import com.foodreels.backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration, login and JWT authentication APIs.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a user",
            description = "Registers a user with the USER role and returns an empty successful response."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Log in",
            description = "Validates email and password and returns a JWT with user details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(response);
    }

}
