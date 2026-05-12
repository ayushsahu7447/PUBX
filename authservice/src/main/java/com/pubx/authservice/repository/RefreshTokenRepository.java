package com.pubx.authservice.repository;

import com.pubx.authservice.entity.RefreshToken;
import com.pubx.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteAllByUser(User user);
}
