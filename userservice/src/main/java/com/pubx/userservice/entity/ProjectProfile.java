package com.pubx.userservice.entity;

import com.pubx.userservice.enums.OrgType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProjectProfile {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "org_name", nullable = false, length = 255)
    private String orgName;

    @Column(unique = true, length = 100)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_type", length = 50)
    private OrgType orgType;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    @Builder.Default
    private String country = "India";

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    @Builder.Default // while building builder will not ignore default value
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}