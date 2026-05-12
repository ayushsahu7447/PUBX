package com.pubx.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonProfileResponse {
    private UUID id;
    private String fullName;
    private String username;
    private String bio;
    private String city;
    private String state;
    private String country;
    private String profileImage;
    private BigDecimal avgRating;
    private Integer totalRatings;
    private Boolean isVerified;
    private List<TopicResponse> topics;
    private String websiteUrl;
    private String linkedinUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String youtubeUrl;
    private List<MediaPostResponse> mediaPosts;
}
