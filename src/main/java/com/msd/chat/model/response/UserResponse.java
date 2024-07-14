package com.msd.chat.model.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
    Long id, String username, String name, String image, UUID chatUUID)
    implements UserResponseInterface {}
