package com.pubx.authservice.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 * @RestControllerAdvice = catches exceptions from ALL controllers
 * Instead of each controller handling errors, this ONE class handles everything.
 *
 * Without this:   { "timestamp": ..., "status": 500, "error": ..., "trace": "com.pubx.auth..." }  ← ugly
 * With this:      { "error": "Email already registered", "status": 409, "timestamp": ... }         ← clean
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // ─────────────────────────────────────────────
    //  Handle validation errors (@Valid failures)
    //  Example: email is blank, password too short
    //  Returns 400 with field-level error messages
    // ─────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validation failed");
        response.put("fields", fieldErrors);
        response.put("timestamp", LocalDateTime.now().toString());

        log.warn("Validation failed: {}", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ─────────────────────────────────────────────
    //  Handle bad credentials (wrong password)
    //  Spring Security throws this from AuthenticationManager
    // ─────────────────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex) {

        Map<String, Object> response = Map.of(
                "status", 401,
                "error", "Invalid email or password",
                "timestamp", LocalDateTime.now().toString()
        );

        log.warn("Bad credentials attempt");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ─────────────────────────────────────────────
    //  Handle our custom RuntimeExceptions
    //  "Email already registered", "Cannot register as ADMIN", etc.
    // ─────────────────────────────────────────────
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {

        // Determine appropriate status code based on message
        HttpStatus status = determineStatus(ex.getMessage());

        Map<String, Object> response = Map.of(
                "status", status.value(),
                "error", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );

        log.error("Runtime exception: {}", ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    // ─────────────────────────────────────────────
    //  Catch-all — anything unexpected
    // ─────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {

        Map<String, Object> response = Map.of(
                "status", 500,
                "error", "Internal server error",
                "timestamp", LocalDateTime.now().toString()
        );

        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // ─────────────────────────────────────────────
    //  Helper — map error messages to HTTP status codes
    // ─────────────────────────────────────────────
    private HttpStatus determineStatus(String message) {
        if (message == null) return HttpStatus.INTERNAL_SERVER_ERROR;

        if (message.contains("already registered")) return HttpStatus.CONFLICT;           // 409
        if (message.contains("not found")) return HttpStatus.NOT_FOUND;                   // 404
        if (message.contains("Invalid") || message.contains("expired")) return HttpStatus.UNAUTHORIZED;  // 401
        if (message.contains("Cannot") || message.contains("disabled")) return HttpStatus.FORBIDDEN;     // 403
        if (message.contains("revoked")) return HttpStatus.UNAUTHORIZED;                  // 401

        return HttpStatus.BAD_REQUEST;  // 400 default
    }
}
