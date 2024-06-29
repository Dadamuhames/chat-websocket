package com.msd.chat.model.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignUpRequest(
        @NotNull @NotEmpty String username,
        @NotNull @NotEmpty String name,
        @NotEmpty @NotNull String password,
        String image) { }
