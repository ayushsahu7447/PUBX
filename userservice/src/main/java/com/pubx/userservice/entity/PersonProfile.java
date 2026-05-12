package com.pubx.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "person_profiles")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PersonProfile {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;    // Same as auth user ID — NOT auto-generated

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "username", unique = true, length = 100)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    @Builder.Default
    private String country = "India";

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "twitter_url", length = 500)
    private String twitterUrl;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    @Builder.Default
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "person_topics",        // ← no schema prefix
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @Builder.Default
    private List<Topic> topics = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
