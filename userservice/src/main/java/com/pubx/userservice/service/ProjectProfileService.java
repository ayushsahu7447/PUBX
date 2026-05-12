package com.pubx.userservice.service;

import com.pubx.userservice.dto.request.CreateProjectProfileRequest;
import com.pubx.userservice.dto.response.MediaPostResponse;
import com.pubx.userservice.dto.response.ProjectProfileResponse;
import com.pubx.userservice.entity.MediaPost;
import com.pubx.userservice.entity.ProjectProfile;
import com.pubx.userservice.repository.MediaPostRepository;
import com.pubx.userservice.repository.ProjectProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectProfileService {

    private final ProjectProfileRepository projectRepo;
    private final MediaPostRepository mediaRepo;

    @Transactional
    public ProjectProfileResponse createProfile(UUID userId, CreateProjectProfileRequest req) {

        if (projectRepo.existsById(userId)) {
            throw new RuntimeException("Profile already exists");
        }

        if (req.getUsername() != null && projectRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        ProjectProfile profile = ProjectProfile.builder()
                .id(userId)
                .orgName(req.getOrgName())
                .username(req.getUsername())
                .description(req.getDescription())
                .orgType(req.getOrgType())
                .city(req.getCity())
                .state(req.getState())
                .websiteUrl(req.getWebsiteUrl())
                .linkedinUrl(req.getLinkedinUrl())
                .build();

        profile = projectRepo.save(profile);
        log.info("Project profile created for userId: {}", userId);
        return toResponse(profile);
    }

    public ProjectProfileResponse getMyProfile(UUID userId) {
        ProjectProfile profile = projectRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toResponse(profile);
    }

    public ProjectProfileResponse getByUsername(String username) {
        ProjectProfile profile = projectRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + username));
        return toResponse(profile);
    }

    @Transactional
    public ProjectProfileResponse updateProfile(UUID userId, CreateProjectProfileRequest req) {
        ProjectProfile profile = projectRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (req.getUsername() != null &&
                !req.getUsername().equals(profile.getUsername()) &&
                projectRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        profile.setOrgName(req.getOrgName());
        profile.setUsername(req.getUsername());
        profile.setDescription(req.getDescription());
        profile.setOrgType(req.getOrgType());
        profile.setCity(req.getCity());
        profile.setState(req.getState());
        profile.setWebsiteUrl(req.getWebsiteUrl());
        profile.setLinkedinUrl(req.getLinkedinUrl());

        return toResponse(projectRepo.save(profile));
    }

    private ProjectProfileResponse toResponse(ProjectProfile p) {
        List<MediaPost> posts = mediaRepo
                .findByOwnerIdAndOwnerTypeAndIsActiveTrueOrderBySortOrderAsc(p.getId(), "PROJECT");

        List<MediaPostResponse> mediaResponses = posts.stream()
                .map(m -> MediaPostResponse.builder()
                        .id(m.getId())
                        .mediaType(m.getMediaType())
                        .mediaUrl(m.getMediaUrl())
                        .thumbnailUrl(m.getThumbnailUrl())
                        .caption(m.getCaption())
                        .durationSecs(m.getDurationSecs())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();

        return ProjectProfileResponse.builder()
                .id(p.getId())
                .orgName(p.getOrgName())
                .username(p.getUsername())
                .description(p.getDescription())
                .orgType(p.getOrgType())
                .city(p.getCity())
                .state(p.getState())
                .country(p.getCountry())
                .logoUrl(p.getLogoUrl())
                .avgRating(p.getAvgRating())
                .totalRatings(p.getTotalRatings())
                .isVerified(p.getIsVerified())
                .websiteUrl(p.getWebsiteUrl())
                .linkedinUrl(p.getLinkedinUrl())
                .mediaPosts(mediaResponses)
                .build();
    }
}