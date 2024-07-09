package com.msd.chat.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProfileEditRequest(
    @NotNull @NotEmpty String name, @NotNull @NotEmpty String username, String image) {}
