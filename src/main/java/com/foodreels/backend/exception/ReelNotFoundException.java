package com.foodreels.backend.exception;

public class ReelNotFoundException extends RuntimeException {

    public ReelNotFoundException(String message) {
        super(message);
    }
}
