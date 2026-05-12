package com.pubx.userservice.repository;

import com.pubx.userservice.entity.MediaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaPostRepository extends JpaRepository<MediaPost, UUID> {

    // Get all active posts for a profile (person or project)
    List<MediaPost> findByOwnerIdAndOwnerTypeAndIsActiveTrueOrderBySortOrderAsc(
            UUID ownerId, String ownerType
    );
}