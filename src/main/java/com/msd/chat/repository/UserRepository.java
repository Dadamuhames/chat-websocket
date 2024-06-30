package com.msd.chat.repository;

import com.msd.chat.domain.UserEntity;
import com.msd.chat.repository.projection.UserProjection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query(value = "SELECT DISTINCT user_entity.*, chat_entity.uuid AS chat_id FROM user_entity " +
            "LEFT JOIN chat_entity ON " +
            "EXISTS (SELECT 1 FROM chat_entity_users c WHERE c.chat_entity_id = chat_entity.id " +
            "AND c.users_id = user_entity.id) " +
            "AND EXISTS (SELECT 1 FROM chat_entity_users c " +
            "WHERE c.chat_entity_id = chat_entity.id AND c.users_id = :id) AND chat_entity.type = 'PRIVATE' " +
            "WHERE user_entity.active = 1 AND user_entity.id != :id AND " +
            "(user_entity.username LIKE %:search% OR user_entity.name LIKE %:search%)",
            nativeQuery = true)
    Page<UserProjection> search(@Param("search") String search, @Param("id") Long id, Pageable pageable);


    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH ChatEntity c ON c.id = :chatId " +
            "WHERE u MEMBER OF c.users AND u.id != :userId")
    UserEntity findCompanionByChat(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
