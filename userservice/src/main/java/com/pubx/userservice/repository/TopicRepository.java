package com.pubx.userservice.repository;

import com.pubx.userservice.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {

    // Get all top-level categories (parent = null)
    List<Topic> findByParentIsNullAndIsActiveTrue();

    // Get subtopics under a parent
    List<Topic> findByParentIdAndIsActiveTrue(UUID parentId);

    // All active topics — used in dropdowns
    List<Topic> findByIsActiveTrue();
}