package com.foodreels.backend.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(
            DuplicateEmailException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    // =========================================================
    // RESOURCE NOT FOUND
    // =========================================================

    @ExceptionHandler(
            {RestaurantNotFoundException.class, FoodNotFoundException.class,
                    ReelNotFoundException.class, UserNotFoundException.class}
    )
    public ResponseEntity<ApiErrorResponse>
            handleRestaurantNotFound(

                    RuntimeException exception,
                    HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }


    // =========================================================
    // RESPONSE STATUS EXCEPTION
    // =========================================================
    //
    // This handles exceptions such as:
    //
    // 400 BAD REQUEST
    // 403 FORBIDDEN
    // 404 NOT FOUND
    //
    // thrown using:
    //
    // new ResponseStatusException(...)
    //
    // =========================================================

    @ExceptionHandler(
            ResponseStatusException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleResponseStatusException(

                    ResponseStatusException exception,
                    HttpServletRequest request) {

        HttpStatus status =
                HttpStatus.resolve(
                        exception
                                .getStatusCode()
                                .value()
                );

        if (status == null) {

            status =
                    HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String message =
                exception.getReason();

        if (message == null
                ||
                message.isBlank()) {

            message =
                    status.getReasonPhrase();
        }

        return buildResponse(
                status,
                message,
                request
        );
    }


    // =========================================================
    // DTO VALIDATION ERRORS
    // =========================================================
    //
    // Handles:
    //
    // @NotNull
    // @NotBlank
    // @NotEmpty
    // @Min
    // @Max
    // @Email
    // etc.
    //
    // =========================================================

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleValidationException(

                    MethodArgumentNotValidException exception,
                    HttpServletRequest request) {

        Map<String, String> fieldErrors =
                new LinkedHashMap<>();


        for (FieldError fieldError
                : exception
                        .getBindingResult()
                        .getFieldErrors()) {

            fieldErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }


        ApiErrorResponse response =
                new ApiErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.BAD_REQUEST.value(),

                        HttpStatus.BAD_REQUEST
                                .getReasonPhrase(),

                        "Validation failed",

                        request.getRequestURI(),

                        fieldErrors
                );


        return ResponseEntity
                .status(
                        HttpStatus.BAD_REQUEST
                )
                .body(
                        response
                );
    }


    // =========================================================
    // CONSTRAINT VIOLATION
    // =========================================================

    @ExceptionHandler(
            ConstraintViolationException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleConstraintViolation(

                    ConstraintViolationException exception,
                    HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                request
        );
    }


    // =========================================================
    // INVALID JSON
    // =========================================================
    //
    // Example:
    //
    // {
    //     "status": "WRONG_STATUS"
    // }
    //
    // when status is an Enum.
    //
    // =========================================================

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleInvalidJson(

                    HttpMessageNotReadableException exception,
                    HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body or malformed JSON",
                request
        );
    }


    // =========================================================
    // MISSING REQUEST PARAMETER
    // =========================================================
    //
    // Example:
    //
    // /api/location/nearby?lat=11.0
    //
    // lng is missing.
    //
    // =========================================================

    @ExceptionHandler(
            MissingServletRequestParameterException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleMissingParameter(

                    MissingServletRequestParameterException exception,
                    HttpServletRequest request) {

        String message =
                "Missing required parameter: "
                        + exception
                                .getParameterName();


        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }


    // =========================================================
    // INVALID PARAMETER TYPE
    // =========================================================
    //
    // Example:
    //
    // /api/orders?page=abc
    //
    // page should be an integer.
    //
    // =========================================================

    @ExceptionHandler(
            MethodArgumentTypeMismatchException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleTypeMismatch(

                    MethodArgumentTypeMismatchException exception,
                    HttpServletRequest request) {

        String message =
                "Invalid value for parameter: "
                        + exception.getName();


        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }


    // =========================================================
    // DATABASE CONSTRAINT ERROR
    // =========================================================
    //
    // Examples:
    //
    // duplicate email
    // duplicate unique value
    // FK constraint violation
    //
    // We DO NOT expose PostgreSQL/Hibernate internal messages.
    //
    // =========================================================

    @ExceptionHandler(
            DataIntegrityViolationException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleDataIntegrityViolation(

                    DataIntegrityViolationException exception,
                    HttpServletRequest request) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "The request conflicts with existing data",
                request
        );
    }


    // =========================================================
    // ILLEGAL ARGUMENT
    // =========================================================

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleIllegalArgument(

                    IllegalArgumentException exception,
                    HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }


    // =========================================================
    // GENERIC EXCEPTION
    // =========================================================
    //
    // LAST SAFETY NET.
    //
    // Do NOT send internal stack traces or exception details
    // to the client.
    //
    // =========================================================

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiErrorResponse>
            handleGenericException(

                    Exception exception,
                    HttpServletRequest request) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request
        );
    }


    // =========================================================
    // RESPONSE BUILDER
    // =========================================================

    private ResponseEntity<ApiErrorResponse>
            buildResponse(

                    HttpStatus status,
                    String message,
                    HttpServletRequest request) {


        ApiErrorResponse response =
                new ApiErrorResponse(

                        LocalDateTime.now(),

                        status.value(),

                        status.getReasonPhrase(),

                        message,

                        request.getRequestURI()
                );


        return ResponseEntity
                .status(
                        status
                )
                .body(
                        response
                );
    }
}
