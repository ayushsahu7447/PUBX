package com.pubx.userservice.controller;

import com.pubx.userservice.config.JwtConfig;
import com.pubx.userservice.dto.request.CreatePersonProfileRequest;
import com.pubx.userservice.dto.response.PersonProfileResponse;
import com.pubx.userservice.service.PersonProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
@Tag(name = "Person Profile", description = "Create and manage talent profiles")
public class PersonProfileController {

    private final PersonProfileService personService;
    private final JwtConfig jwtConfig;

    // Helper — extract userId from "Bearer eyJ..." header
    private UUID getUserId(String authHeader) {
        return jwtConfig.extractUserId(authHeader.substring(7));
    }

    @PostMapping("/profile")
    @Operation(summary = "Create my person profile (first time setup)")
    public ResponseEntity<PersonProfileResponse> createProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreatePersonProfileRequest request) {

        UUID userId = getUserId(authHeader);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(personService.createProfile(userId, request));
    }

    @GetMapping("/profile/me")
    @Operation(summary = "Get my own profile")
    public ResponseEntity<PersonProfileResponse> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = getUserId(authHeader);
        return ResponseEntity.ok(personService.getMyProfile(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my profile")
    public ResponseEntity<PersonProfileResponse> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreatePersonProfileRequest request) {

        UUID userId = getUserId(authHeader);
        return ResponseEntity.ok(personService.updateProfile(userId, request));
    }

    @GetMapping("/{username}")
    @Operation(summary = "View anyone's public profile by username")
    public ResponseEntity<PersonProfileResponse> getByUsername(
            @PathVariable String username) {
        return ResponseEntity.ok(personService.getByUsername(username));
    }
}