package com.msd.chat.model.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LoginRequest(@NotNull @NotEmpty String username, @NotNull @NotEmpty String password) {
}
