package com.pubx.authservice.service;

import com.pubx.authservice.config.JwtService;
import com.pubx.authservice.dto.*;
import com.pubx.authservice.entity.RefreshToken;
import com.pubx.authservice.entity.User;
import com.pubx.authservice.enums.Role;
import com.pubx.authservice.repository.RefreshTokenRepository;
import com.pubx.authservice.repository.UserRepository;
import com.pubx.authservice.util.TokenHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ─────────────────────────────────────────────
    //  In-memory token blacklist (swap with Redis later)
    //  TODO: Replace with Redis when installed
    //  This works for single-instance dev environment
    // ─────────────────────────────────────────────
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    // ─────────────────────────────────────────────
    //  REGISTER
    // ─────────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Block ADMIN registration via API
        if (request.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot register as ADMIN");
        }

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isVerified(false)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {} with role: {}", user.getEmail(), user.getRole());

        return generateTokensAndBuildResponse(user, "Registration successful");
    }

    // ─────────────────────────────────────────────
    //  LOGIN
    // ─────────────────────────────────────────────
    @Transactional
    public AuthResponse login(LoginRequest request) {

        // Spring Security verifies credentials
        // Throws BadCredentialsException if wrong password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        // Load user
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is disabled. Contact support.");
        }

        // Remove old refresh tokens (single active session)
        refreshTokenRepository.deleteAllByUser(user);

        log.info("User logged in: {}", user.getEmail());
        return generateTokensAndBuildResponse(user, "Login successful");
    }

    // ─────────────────────────────────────────────
    //  LOGOUT
    //  Blacklists access token + removes refresh token
    //
    //  Using in-memory Set now.
    //  When Redis is installed → swap to:
    //    redisTemplate.opsForValue().set(
    //      "blacklist:" + token, "1", remainingTTL, TimeUnit.MS
    //    )
    // ─────────────────────────────────────────────
    @Transactional
    public void logout(String accessToken, String refreshToken) {

        // Blacklist access token
        if (accessToken != null && !accessToken.isBlank()) {
            tokenBlacklist.add(accessToken);
            log.info("Access token blacklisted");
        }

        // Delete refresh token from DB
        if (refreshToken != null && !refreshToken.isBlank()) {
            String tokenHash = TokenHashUtil.hashToken(refreshToken);
            refreshTokenRepository.findByTokenHash(tokenHash)
                    .ifPresent(refreshTokenRepository::delete);
        }

        log.info("User logged out successfully");
    }

    // ─────────────────────────────────────────────
    //  Check if token is blacklisted
    //  Called by JWT filter (we'll build this next)
    // ─────────────────────────────────────────────
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    // ─────────────────────────────────────────────
    //  REFRESH TOKEN (with rotation)
    //  Old refresh token → deleted
    //  New refresh token → generated
    //  This prevents token reuse attacks
    // ─────────────────────────────────────────────
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {

        // 1. Validate JWT
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // 2. Must be REFRESH type
        if (!"REFRESH".equals(jwtService.extractTokenType(refreshToken))) {
            throw new RuntimeException("Token is not a refresh token");
        }

        // 3. Find in DB
        String tokenHash = TokenHashUtil.hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Refresh token not found in DB"));

        // 4. Check revoked
        if (storedToken.getIsRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        // 5. Check expired
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        // 6. Load user
        User user = storedToken.getUser();
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // 7. Delete old token (rotation)
        refreshTokenRepository.delete(storedToken);

        log.info("Token refreshed for user: {}", user.getEmail());

        // 8. Generate new pair
        return generateTokensAndBuildResponse(user, "Token refreshed successfully");
    }

    // ─────────────────────────────────────────────
    //  HELPER — DRY: used by register, login, refresh
    // ─────────────────────────────────────────────
    private AuthResponse generateTokensAndBuildResponse(User user, String message) {

        // Generate access token
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );

        // Generate refresh token
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        // Store refresh token hash in DB
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .tokenHash(TokenHashUtil.hashToken(refreshToken))
                .expiresAt(
                        jwtService.extractExpiry(refreshToken)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                )
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId().toString())
                .email(user.getEmail())
                .role(user.getRole())
                .message(message)
                .build();
    }
}