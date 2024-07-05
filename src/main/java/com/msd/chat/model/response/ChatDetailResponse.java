package com.msd.chat.model.response;


import lombok.Builder;

import java.util.UUID;

@Builder
public record ChatDetailResponse(Long id, UUID uuid, String name, String image, UserResponse user) {}
