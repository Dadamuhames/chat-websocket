package com.msd.chat.model.request;

import lombok.Builder;

@Builder
public record PrivateChatCreateRequest(Long userId) {}
