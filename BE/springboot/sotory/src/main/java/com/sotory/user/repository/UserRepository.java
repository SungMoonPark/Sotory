package com.sotory.user.repository;

import com.sotory.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByNickname(String nickname);
    Optional<User> findByUserId(UUID userId);
        // ✅ userId로 nickname만 조회
    @Query("SELECT u.nickname FROM User u WHERE u.userId = :userId")
    Optional<String> findNicknameByUserId(@Param("userId") UUID userId);
}
