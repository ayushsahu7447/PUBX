package com.pubx.userservice.service;

import com.pubx.userservice.dto.response.TopicResponse;
import com.pubx.userservice.entity.Topic;
import com.pubx.userservice.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    // Returns all topics grouped — parents with their children
    public List<TopicResponse> getAllTopics() {
        List<Topic> parents = topicRepository.findByParentIsNullAndIsActiveTrue();

        return parents.stream().map(parent -> {
            List<Topic> children = topicRepository
                    .findByParentIdAndIsActiveTrue(parent.getId());

            List<TopicResponse> childResponses = children.stream()
                    .map(c -> TopicResponse.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .slug(c.getSlug())
                            .parentId(parent.getId())
                            .build())
                    .toList();

            return TopicResponse.builder()
                    .id(parent.getId())
                    .name(parent.getName())
                    .slug(parent.getSlug())
                    .children(childResponses)
                    .build();
        }).toList();
    }
}