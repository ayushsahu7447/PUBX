package com.pubx.authservice.config;

import com.pubx.authservice.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/*
 * What is this filter?
 * --------------------
 * Every HTTP request passes through this BEFORE reaching your controller.
 *
 * Flow:
 *   Request comes in
 *     → JwtAuthFilter checks for "Authorization: Bearer xxx" header
 *       → If no token → skip (let SecurityConfig decide if path is public)
 *       → If token present → validate it
 *         → If valid → set user info in SecurityContext (user is "logged in")
 *         → If invalid → reject with 401
 *
 * OncePerRequestFilter = runs exactly once per request (not multiple times)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // ─────────────────────────────────────────
        //  Step 1: Get the Authorization header
        //  Format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        // ─────────────────────────────────────────
        final String authHeader = request.getHeader("Authorization");

        // No header or doesn't start with "Bearer " → skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract raw token (remove "Bearer " prefix)
        final String token = authHeader.substring(7);

        try {
            // ─────────────────────────────────────────
            //  Step 2: Validate the JWT token
            // ─────────────────────────────────────────
            if (!jwtService.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // ─────────────────────────────────────────
            //  Step 3: Check if token is blacklisted (logout)
            // ─────────────────────────────────────────
            if (authService.isTokenBlacklisted(token)) {
                log.warn("Blacklisted token used");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Token has been revoked\"}");
                return;
            }

            // ─────────────────────────────────────────
            //  Step 4: Must be ACCESS type (not REFRESH)
            // ─────────────────────────────────────────
            String tokenType = jwtService.extractTokenType(token);
            if (!"ACCESS".equals(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }

            // ─────────────────────────────────────────
            //  Step 5: Extract user info from token
            // ─────────────────────────────────────────
            UUID userId = jwtService.extractUserId(token);
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            // ─────────────────────────────────────────
            //  Step 6: Set authentication in SecurityContext
            //
            //  This tells Spring: "This request is from an
            //  authenticated user with this email and role"
            //
            //  After this, @PreAuthorize("hasRole('PERSON')")
            //  and other security annotations will work
            // ─────────────────────────────────────────
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                email,          // principal (who)
                                null,           // credentials (not needed, JWT already verified)
                                authorities     // roles
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Store userId in request attribute — controllers can access it
                request.setAttribute("userId", userId.toString());
                request.setAttribute("userRole", role);
            }

        } catch (Exception e) {
            log.error("JWT Filter error: {}", e.getMessage());
            // Don't block request — let SecurityConfig handle unauthorized
        }

        // Continue to next filter / controller
        filterChain.doFilter(request, response);
    }
}