package com.msd.chat.model.response;


import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MessageReadResponse(UUID chatUUID, List<UUID> messagesUUID) {}
