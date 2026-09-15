package com.foodreels.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodreels.backend.dto.UserRequestDTO;
import com.foodreels.backend.dto.UserResponseDTO;
import com.foodreels.backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management APIs.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create user
    @Operation(
            summary = "Create a user",
            description = "Creates a user from the supplied details. Requires the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
        @Valid @RequestBody UserRequestDTO userRequestDTO) {
            
            UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
        
    }

    // Get all users
    @Operation(
            summary = "Get all users",
            description = "Returns all users. Requires the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get user by id
    @Operation(
            summary = "Get a user",
            description = "Returns the user identified by its ID. Requires the ADMIN role.",
            parameters = {
                    @Parameter(name = "id", description = "User ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long id) {

                UserResponseDTO userResponseDTO = userService.getUserById(id);
                return ResponseEntity.ok(userResponseDTO);

        //return userService.getUserById(id);
    }

    // Update user
    @Operation(
            summary = "Update a user",
            description = "Updates the specified user using the supplied details. Requires the ADMIN role.",
            parameters = {
                    @Parameter(name = "id", description = "User ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO userRequestDTO) {

                UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
                return ResponseEntity.ok(updatedUser);
        //return userService.updateUser(id, userRequestDTO);
    }

    // Delete user
    @Operation(
            summary = "Delete a user",
            description = "Deletes the specified user. Requires the ADMIN role.",
            parameters = {
                    @Parameter(name = "id", description = "User ID", example = "1")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}