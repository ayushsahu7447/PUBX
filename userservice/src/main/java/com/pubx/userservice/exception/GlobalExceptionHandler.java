package com.pubx.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice = catches exceptions from ALL controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles @Valid failures — e.g. blank name, invalid email
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation failed",
                "details", errors,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // Handles RuntimeException — e.g. "Profile not found"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}