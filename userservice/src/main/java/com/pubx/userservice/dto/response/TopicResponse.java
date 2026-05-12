package com.pubx.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicResponse {
    private UUID id;
    private String name;
    private String slug;
    private UUID parentId;
    private List<TopicResponse> children;
}
