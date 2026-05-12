package com.pubx.authservice.config;


    /*
     * What is this file?
     * --------
     * Spring Security by default BLOCKS everything.
     * If you don't configure this, even /api/auth/register will return 401.
     *
     * Here we say:
     *   - /api/auth/** → OPEN (anyone can register/login)
     *   - /swagger-ui/** → OPEN (for API docs)
     *   - Everything else → LOCKED (need JWT)
     *
     * We also:
     *   - Disable CSRF (not needed for REST APIs — CSRF is for browser forms)
     *   - Set STATELESS session (no server-side session — JWT handles auth)
     */

import com.pubx.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig{
        private final UserRepository userRepository;
        private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(UserRepository userRepository, @Lazy JwtAuthFilter jwtAuthFilter) {
        this.userRepository = userRepository;
        this.jwtAuthFilter = jwtAuthFilter;
    }
        // ─────────────────────────────────────────────
        //  THE MAIN SECURITY RULES
        //  Every HTTP request passes through this chain
        // ─────────────────────────────────────────────
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            // PUBLIC — no token needed
                            .requestMatchers(
                                    "/api/v1/auth/**"
                            ).permitAll()

                            // Swagger — open for API docs
                            .requestMatchers(
                                    "/swagger-ui.html",
                                    "/swagger-ui/**",
                                    "/api-docs/**",
                                    "/v3/api-docs/**"
                            ).permitAll()

                            // Health check
                            .requestMatchers("/actuator/**").permitAll()

                            // Everything else — MUST have valid JWT
                            .anyRequest().authenticated()
                    )

                    // STATELESS — no sessions, JWT handles everything
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )

                    .authenticationProvider(authenticationProvider())
                    // ← NEW: Add our JWT filter BEFORE Spring's default auth filter
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        // ─────────────────────────────────────────────
        //  BCrypt — password hashing
        //  "abc123" → "$2a$10$xK8Lm..." (different every time)
        // ─────────────────────────────────────────────
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        // ─────────────────────────────────────────────
        //  UserDetailsService — loads user from DB
        //  Spring Security calls this during authentication
        //
        //  FIX: using getPassword() NOT getPasswordHash()
        //  because our User entity field is called "password"
        // ─────────────────────────────────────────────
        @Bean
        public UserDetailsService userDetailsService() {
            return email -> userRepository.findByEmail(email)
                    .map(user -> org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password(user.getPassword())     // ← FIXED: matches our User entity
                            .roles(user.getRole().name())
                            .build()
                    )
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found: " + email)
                    );
        }

        // ─────────────────────────────────────────────
        //  AuthenticationProvider — connects
        //  UserDetailsService + PasswordEncoder
        // ─────────────────────────────────────────────
        @Bean
        public AuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(userDetailsService());
            provider.setPasswordEncoder(passwordEncoder());
            return provider;
        }

        // ─────────────────────────────────────────────
        //  AuthenticationManager — used in AuthService
        //  to trigger authentication programmatically
        // ─────────────────────────────────────────────
        @Bean
        public AuthenticationManager authenticationManager(
                AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
}