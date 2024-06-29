package com.msd.chat.repository;

import com.msd.chat.domain.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.username LIKE 'search%' OR u.name LIKE 'search%'")
    Page<UserEntity> search(@Param("search") String search, @Param("currentUserId") Long currentUserId, Pageable pageable);
}
