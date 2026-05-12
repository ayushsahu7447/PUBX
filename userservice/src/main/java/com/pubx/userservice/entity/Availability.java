package com.pubx.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "availability", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"person_id", "date"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(length = 255)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}