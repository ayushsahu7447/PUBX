package com.pubx.userservice.controller;

import com.pubx.userservice.dto.response.TopicResponse;
import com.pubx.userservice.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "Get topic taxonomy for profile setup")
public class TopicController {

    private final TopicService topicService;

    // Public endpoint — no auth needed
    // Returns all parent topics with their children
    @GetMapping
    @Operation(summary = "Get all topics grouped by category")
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }
}
