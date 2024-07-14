package com.msd.chat.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record GroupChatCreateRequest(
    @NotNull @NotEmpty String name, @NotNull @NotEmpty List<Long> usersId, String image) {}
