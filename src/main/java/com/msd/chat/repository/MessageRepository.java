package com.msd.chat.repository;

import com.msd.chat.domain.MessageEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    Page<MessageEntity> findByChatUuid(UUID uuid, Pageable pageable);


    @Query("SELECT m FROM MessageEntity m " +
            "LEFT JOIN UserEntity u ON u.id = :userId " +
            "WHERE m.chat.id = :chatId AND u NOT MEMBER OF m.readByUsers")
    List<MessageEntity> findNewForUserByChatId(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
