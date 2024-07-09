package com.msd.chat.mapper;

import com.msd.chat.domain.MessageEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.response.ChatResponse;
import com.msd.chat.model.response.MessageResponse;
import com.msd.chat.model.response.MessageSocketResponse;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
  private final ChatMapper chatMapper;

  public MessageResponse toResponse(final MessageEntity message, final UserEntity user) {
    Boolean isUsersMessage = Objects.equals(user.getId(), message.getFromUser().getId());

    Set<UserEntity> readByUsers = message.getReadByUsers();

    Boolean isReadByUser =
        readByUsers != null
            && isUsersMessage
            && !readByUsers.stream().map(UserEntity::getId).toList().isEmpty();

    return MessageResponse.builder()
        .id(message.getId())
        .uuid(message.getUuid())
        .chatUUID(message.getChat().getUuid())
        .message(message.getMessage())
        .fromUserId(message.getFromUser().getId())
        .createdAt(message.getCreatedAt())
        .isMyMessage(isUsersMessage)
        .isReadByUser(isReadByUser)
        .build();
  }

  public MessageSocketResponse toSocketResponse(
      final MessageEntity message, final Long newMessageCount, final Boolean isChatNew) {
    ChatResponse chat = chatMapper.toResponse(message.getChat(), message.getFromUser());

    return MessageSocketResponse.builder()
        .id(message.getId())
        .uuid(message.getUuid())
        .message(message.getMessage())
        .createdAt(message.getCreatedAt().toString())
        .newMessagesCount(newMessageCount)
        .isChatNew(isChatNew)
        .chat(chat)
        .build();
  }
}
