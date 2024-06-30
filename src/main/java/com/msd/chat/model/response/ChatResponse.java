package com.msd.chat.model.response;


import com.msd.chat.domain.enums.ChatTypes;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChatResponse(Long id, Long userId, UUID uuid, String name, String image, String username, ChatTypes type) {}
