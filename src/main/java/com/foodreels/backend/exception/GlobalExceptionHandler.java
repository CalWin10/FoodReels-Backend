package com.foodreels.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@RestControllerAdvice

public class GlobalExceptionHandler {

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> {
                                        errors.put(
                                                        error.getField(),
                                                        error.getDefaultMessage());
                                });

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errors);
        }

        @ExceptionHandler({
                        RestaurantNotFoundException.class,
                        FoodNotFoundException.class,
                        ReelNotFoundException.class
        })
        public ResponseEntity<String> handleResourceNotFound(
                        RuntimeException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(DuplicateEmailException.class)
        public ResponseEntity<String> handleDuplicateEmail(
                        DuplicateEmailException ex) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<String> handleInvalidCredentials(
                        InvalidCredentialsException ex) {

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ex.getMessage());
        }
}

