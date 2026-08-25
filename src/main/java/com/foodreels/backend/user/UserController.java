package com.foodreels.backend.user;

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

import com.foodreels.backend.user.UserRequestDTO;
import com.foodreels.backend.user.UserResponseDTO;
import com.foodreels.backend.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
        @Valid @RequestBody UserRequestDTO userRequestDTO) {
            
            UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
        
    }

    // Get all users
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long id) {

                UserResponseDTO userResponseDTO = userService.getUserById(id);
                return ResponseEntity.ok(userResponseDTO);

        //return userService.getUserById(id);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDTO userRequestDTO) {

                UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
                return ResponseEntity.ok(updatedUser);
        //return userService.updateUser(id, userRequestDTO);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

