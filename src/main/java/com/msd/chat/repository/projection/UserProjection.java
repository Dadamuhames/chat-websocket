package com.msd.chat.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserProjection {
    Long getId();

    UUID getChatUUID();

    String getUsername();

    String getName();

    String getImage();

    LocalDateTime getJoined_At();
}
