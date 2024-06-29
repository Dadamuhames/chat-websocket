package com.msd.chat.repository.projection;

import com.msd.chat.domain.UserEntity;

import java.util.UUID;

public record UserProjection(UserEntity user, UUID chatUUID) {
}
