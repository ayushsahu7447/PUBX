package com.pubx.authservice.controller;
import com.pubx.authservice.dto.*;
import com.pubx.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Register, Login, Logout, Refresh token")
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
    @Operation(summary = "Register a new user (PERSON or PROJECT)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout — blacklists access token, deletes refresh token")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        String accessToken = authHeader.substring(7);   // Remove "Bearer "
        String refreshToken = body.get("refreshToken");

        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Get new tokens using refresh token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {

        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "service", "auth-service",
                "status", "running"
        ));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info from JWT token")
    public ResponseEntity<Map<String, String>> me(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");

        return ResponseEntity.ok(Map.of(
                "userId", userId != null ? userId : "unknown",
                "role", role != null ? role : "unknown"
        ));
    }
}