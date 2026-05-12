package com.pubx.userservice.dto.response;

import com.pubx.userservice.enums.MediaType;
import lombok.Builder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaPostResponse {
    private UUID id;
    private MediaType mediaType;
    private String mediaUrl;
    private String thumbnailUrl;
    private String caption;
    private Integer durationSecs;
    private LocalDateTime createdAt;
}