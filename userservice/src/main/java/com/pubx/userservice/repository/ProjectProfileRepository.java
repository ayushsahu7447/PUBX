package com.pubx.userservice.repository;

import com.pubx.userservice.entity.ProjectProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectProfileRepository extends JpaRepository<ProjectProfile, UUID> {

    Optional<ProjectProfile> findByUsername(String username);

    boolean existsByUsername(String username);
}