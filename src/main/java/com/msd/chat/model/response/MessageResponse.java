package com.msd.chat.model.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageResponse(
    Long id,
    UUID uuid,
    UUID chatUUID,
    String message,
    Long fromUserId,
    UserResponse fromUser,
    Boolean isMyMessage,
    Boolean isReadByUser,
    LocalDateTime createdAt) {}
