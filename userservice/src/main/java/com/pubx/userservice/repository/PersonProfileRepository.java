package com.pubx.userservice.repository;

import com.pubx.userservice.entity.PersonProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonProfileRepository extends JpaRepository<PersonProfile, UUID> {

    Optional<PersonProfile> findByUsername(String username);

    boolean existsByUsername(String username);
}