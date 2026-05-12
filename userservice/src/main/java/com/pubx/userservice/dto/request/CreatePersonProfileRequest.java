package com.pubx.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreatePersonProfileRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    private String fullName;

    @Size(max = 100, message = "Username max 100 characters")
    private String username;

    @Size(max = 1000, message = "Bio too long")
    private String bio;

    private String city;
    private String state;

    @Size(max = 5, message = "Maximum 5 topics allowed")
    @NotEmpty(message = "Select at least 1 topic")
    private List<UUID> topicIds;

    private String websiteUrl;
    private String linkedinUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String youtubeUrl;
}
