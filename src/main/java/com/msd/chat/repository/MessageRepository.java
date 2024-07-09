package com.msd.chat.repository;

import com.msd.chat.domain.MessageEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

  @EntityGraph(attributePaths = {"readByUsers"})
  Page<MessageEntity> findByChatUuid(UUID uuid, Pageable pageable);

  @Query(
      "SELECT m FROM MessageEntity m "
          + "JOIN UserEntity u ON u.id = :userId "
          + "WHERE m.chat.uuid = :chatUUID AND u NOT MEMBER OF m.readByUsers "
          + "AND u MEMBER OF m.chat.users AND u != m.fromUser")
  List<MessageEntity> findNewForUserByChatId(
      @Param("chatUUID") UUID chatUUID, @Param("userId") Long userId);

  @Query(
      "SELECT m FROM MessageEntity m JOIN UserEntity u ON u.id = :userId "
          + "WHERE m.uuid = :uuid AND u MEMBER OF m.chat.users AND m.fromUser != u")
  Optional<MessageEntity> findByUUIDAndUserId(
      @Param("uuid") UUID uuid, @Param("userId") Long userId);

  @Query(
      "SELECT COUNT(m) FROM MessageEntity m JOIN UserEntity u ON u.id = :userId "
          + "WHERE u MEMBER OF m.chat.users AND m.chat.uuid = :chatUUID AND "
          + "u NOT MEMBER OF m.readByUsers AND m.fromUser.id != :userId")
  Long countNewMessagedByChatAndUser(
      @Param("chatUUID") UUID chatUUID, @Param("userId") Long userId);
}
