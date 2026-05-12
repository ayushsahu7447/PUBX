package com.pubx.userservice.dto.response;

import com.pubx.userservice.enums.OrgType;
import lombok.Builder;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectProfileResponse {
    private UUID id;
    private String orgName;
    private String username;
    private String description;
    private OrgType orgType;
    private String city;
    private String state;
    private String country;
    private String logoUrl;
    private BigDecimal avgRating;
    private Integer totalRatings;
    private Boolean isVerified;
    private String websiteUrl;
    private String linkedinUrl;
    private List<MediaPostResponse> mediaPosts;
}