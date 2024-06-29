package com.msd.chat.model.response;

import lombok.Builder;

@Builder
public record TokensResponse(String accessToken, String refreshToken) {}
