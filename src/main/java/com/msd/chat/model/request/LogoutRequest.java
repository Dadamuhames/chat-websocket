package com.msd.chat.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LogoutRequest(
    @NotEmpty @NotNull String refreshToken,
    String deviceToken) {} // TODO required
