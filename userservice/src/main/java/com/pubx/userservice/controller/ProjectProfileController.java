package com.pubx.userservice.controller;

import com.pubx.userservice.config.JwtConfig;
import com.pubx.userservice.dto.request.CreateProjectProfileRequest;
import com.pubx.userservice.dto.response.ProjectProfileResponse;
import com.pubx.userservice.service.ProjectProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project Profile", description = "Create and manage organization profiles")
public class ProjectProfileController {

    private final ProjectProfileService projectService;
    private final JwtConfig jwtConfig;

    private UUID getUserId(String authHeader) {
        return jwtConfig.extractUserId(authHeader.substring(7));
    }

    @PostMapping("/profile")
    @Operation(summary = "Create project profile (first time setup)")
    public ResponseEntity<ProjectProfileResponse> createProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateProjectProfileRequest request) {
        UUID userId = getUserId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProfile(userId, request));
    }

    @GetMapping("/profile/me")
    @Operation(summary = "Get my project profile")
    public ResponseEntity<ProjectProfileResponse> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = getUserId(authHeader);
        return ResponseEntity.ok(projectService.getMyProfile(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my project profile")
    public ResponseEntity<ProjectProfileResponse> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateProjectProfileRequest request) {
        UUID userId = getUserId(authHeader);
        return ResponseEntity.ok(projectService.updateProfile(userId, request));
    }

    @GetMapping("/{username}")
    @Operation(summary = "View project's public profile")
    public ResponseEntity<ProjectProfileResponse> getByUsername(
            @PathVariable String username) {
        return ResponseEntity.ok(projectService.getByUsername(username));
    }
}