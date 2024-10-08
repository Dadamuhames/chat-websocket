package com.msd.chat.model.response;

import com.msd.chat.domain.enums.ChatTypes;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChatResponse(
    Long id,
    Long userId,
    UUID uuid,
    String name,
    String image,
    String username,
    ChatTypes type,
    String lastMessage,
    Long newMessagesCount) {}
