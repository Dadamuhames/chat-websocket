package com.msd.chat.model.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageCreateRequest(@NotNull UUID chatUUID, String message, String image) {
    @AssertTrue(message = "Message or Image required")
    private boolean isMessageOrImage() {
        return ((image == null || image.isEmpty()) && (message != null && !message.isEmpty()))
                || ((image != null && !image.isEmpty()) && (message == null || message.isEmpty()));
    }
}
