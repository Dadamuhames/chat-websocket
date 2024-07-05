package com.msd.chat.repository;

import com.msd.chat.domain.ChatEntity;
import com.msd.chat.repository.projection.ChatProjection;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

  @Query(
      "SELECT c.uuid FROM ChatEntity c JOIN c.users u ON (u.id = :userId OR u.id = :authUserId) " +
              "WHERE c.type = 'PRIVATE'")
  UUID findChatUUIDByUserIdAndAuthUserAndPrivate(
      @Param("userId") Long userId, @Param("authUserId") Long authUserId);

  @Query(
      "SELECT COUNT(c) > 0 FROM ChatEntity c "
          + "LEFT JOIN UserEntity u1 ON u1.id = :userId "
          + "LEFT JOIN UserEntity u2 ON u2.id = :authUserId "
          + "WHERE c.type = 'PRIVATE' AND u1 MEMBER OF c.users AND u2 MEMBER OF c.users")
  Boolean existsByUserIdAndAuthUserAndPrivate(
      @Param("userId") Long userId, @Param("authUserId") Long authUserId);



  @Query("SELECT COUNT(c) > 0 FROM ChatEntity c JOIN c.users u ON u.id = :userId WHERE c.uuid = :uuid")
  Boolean existsByUserIdAndUUID(@Param("uuid") UUID uuid, @Param("userId") Long userId);


  @Query("SELECT c FROM ChatEntity c JOIN c.users u ON u.id = :userId WHERE c.uuid = :uuid")
  Optional<ChatEntity> findByUserIdAndUUID(@Param("uuid") UUID uuid, @Param("userId") Long userId);

  // TODO order by last message
  @Query(value = "SELECT DISTINCT c.id, c.uuid, c.name, c.image, c.type, " +
          "u.name AS userFullName, u.username, u.id AS userId, u.image AS userImage, COUNT(m.id) AS newMessagesCount " +
          "FROM chat_entity c " +
          "LEFT JOIN chat_entity_users c_u ON " +
          "(c_u.chat_entity_id = c.id AND c_u.users_id != :userId AND c.type = 'PRIVATE') " +
          "LEFT JOIN user_entity u ON u.id = c_u.users_id " +
          "LEFT JOIN message_entity m ON m.chat_id = c.id AND " +
          "NOT EXISTS (SELECT 1 FROM message_entity_read_by_users m_read_by " +
          "WHERE m_read_by.message_entity_id = m.id AND m_read_by.read_by_users_id = :userId) " +
          "WHERE c.active = 1 AND EXISTS " +
          "(SELECT 1 FROM chat_entity_users c_u2 WHERE c_u2.chat_entity_id = c.id AND c_u2.users_id = :userId) " +
          "GROUP BY  c.id, c.uuid, c.name, c.image, c.type, u.name, u.username, u.id, u.image", nativeQuery = true)
  Page<ChatProjection> findByUserId(@Param("userId") Long userId, Pageable pageable);


  @Query("SELECT c FROM ChatEntity c WHERE c.uuid = :uuid AND :userId IN (SELECT u.id FROM UserEntity u WHERE u MEMBER OF c.users)")
  Optional<ChatEntity> findByUUIDAndUserId(@Param("uuid") UUID uuid, @Param("userId") Long userId);
}
