package com.msd.chat.model.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageSocketResponse(
    Long id,
    UUID uuid,
    String message,
    ChatResponse chat,
    Boolean isChatNew,
    LocalDateTime createdAt) {}
