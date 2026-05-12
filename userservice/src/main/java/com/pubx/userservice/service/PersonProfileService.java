package com.pubx.userservice.service;

import com.pubx.userservice.dto.request.CreatePersonProfileRequest;
import com.pubx.userservice.dto.response.MediaPostResponse;
import com.pubx.userservice.dto.response.PersonProfileResponse;
import com.pubx.userservice.dto.response.TopicResponse;
import com.pubx.userservice.entity.MediaPost;
import com.pubx.userservice.entity.PersonProfile;
import com.pubx.userservice.entity.Topic;
import com.pubx.userservice.repository.MediaPostRepository;
import com.pubx.userservice.repository.PersonProfileRepository;
import com.pubx.userservice.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonProfileService {

    private final PersonProfileRepository personRepo;
    private final TopicRepository topicRepo;
    private final MediaPostRepository mediaRepo;

    // ── Create profile ───────────────────────────────
    @Transactional
    public PersonProfileResponse createProfile(
            UUID userId, CreatePersonProfileRequest req) {

        // Can't create twice
        if (personRepo.existsById(userId)) {
            throw new RuntimeException("Profile already exists");
        }

        // Username taken?
        if (req.getUsername() != null &&
                personRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Max 5 topics
        if (req.getTopicIds().size() > 5) {
            throw new RuntimeException("Maximum 5 topics allowed");
        }

        // Load topic entities
        List<Topic> topics = topicRepo.findAllById(req.getTopicIds());
        if (topics.size() != req.getTopicIds().size()) {
            throw new RuntimeException("One or more topics not found");
        }

        PersonProfile profile = PersonProfile.builder()
                .id(userId)
                .fullName(req.getFullName())
                .username(req.getUsername())
                .bio(req.getBio())
                .city(req.getCity())
                .state(req.getState())
                .topics(topics)
                .websiteUrl(req.getWebsiteUrl())
                .linkedinUrl(req.getLinkedinUrl())
                .twitterUrl(req.getTwitterUrl())
                .instagramUrl(req.getInstagramUrl())
                .youtubeUrl(req.getYoutubeUrl())
                .build();

        PersonProfile saved = personRepo.save(profile);
        log.info("Person profile created for userId: {}", userId);

        return toResponse(saved);
    }

    // ── Get my profile ───────────────────────────────
    public PersonProfileResponse getMyProfile(UUID userId) {
        PersonProfile profile = personRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toResponse(profile);
    }

    // ── Get public profile by username ───────────────
    public PersonProfileResponse getByUsername(String username) {
        PersonProfile profile = personRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + username));
        return toResponse(profile);
    }
    @Transactional
    public PersonProfileResponse updateProfile(
            UUID userId, CreatePersonProfileRequest req) {

        PersonProfile profile = personRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Check username not taken by someone else
        if (req.getUsername() != null &&
                !req.getUsername().equals(profile.getUsername()) &&
                personRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (req.getTopicIds().size() > 5) {
            throw new RuntimeException("Maximum 5 topics allowed");
        }

        List<Topic> topics = topicRepo.findAllById(req.getTopicIds());

        profile.setFullName(req.getFullName());
        profile.setUsername(req.getUsername());
        profile.setBio(req.getBio());
        profile.setCity(req.getCity());
        profile.setState(req.getState());
        profile.setTopics(topics);
        profile.setWebsiteUrl(req.getWebsiteUrl());
        profile.setLinkedinUrl(req.getLinkedinUrl());
        profile.setTwitterUrl(req.getTwitterUrl());
        profile.setInstagramUrl(req.getInstagramUrl());
        profile.setYoutubeUrl(req.getYoutubeUrl());

        return toResponse(personRepo.save(profile));
    }

    private PersonProfileResponse toResponse(PersonProfile p) {
        List<MediaPost> posts = mediaRepo
                .findByOwnerIdAndOwnerTypeAndIsActiveTrueOrderBySortOrderAsc(
                        p.getId(), "PERSON");

        List<TopicResponse> topicResponses = p.getTopics().stream()
                .map(t -> TopicResponse.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .slug(t.getSlug())
                        .build())
                .toList();

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

        return PersonProfileResponse.builder()
                .id(p.getId())
                .fullName(p.getFullName())
                .username(p.getUsername())
                .bio(p.getBio())
                .city(p.getCity())
                .state(p.getState())
                .country(p.getCountry())
                .profileImage(p.getProfileImage())
                .avgRating(p.getAvgRating())
                .totalRatings(p.getTotalRatings())
                .isVerified(p.getIsVerified())
                .topics(topicResponses)
                .websiteUrl(p.getWebsiteUrl())
                .linkedinUrl(p.getLinkedinUrl())
                .twitterUrl(p.getTwitterUrl())
                .instagramUrl(p.getInstagramUrl())
                .youtubeUrl(p.getYoutubeUrl())
                .mediaPosts(mediaResponses)
                .build();
    }
}