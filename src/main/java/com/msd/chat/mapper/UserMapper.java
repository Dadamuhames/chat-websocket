package com.msd.chat.mapper;


import com.msd.chat.domain.ChatEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.request.SignUpRequest;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.repository.projection.UserProjection;
import com.msd.chat.service.file.FileGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final FileGetService fileGetService;
    private final PasswordEncoder passwordEncoder;

    public UserEntity fromSignUpRequest(final SignUpRequest signUpRequest) {
        String password = passwordEncoder.encode(signUpRequest.password());

        return UserEntity.builder()
                .username(signUpRequest.username())
                .name(signUpRequest.name())
                .password(password)
                .image(signUpRequest.image())
                .active(true)
                .build();
    }


    public UserResponse toResponse(final UserProjection userProjection) {
        UUID chatUUID = userProjection.getChatUUID();
        String imgPath = null;

        if (!userProjection.getImage().isEmpty()) {
            imgPath = fileGetService.getFileAbsoluteUrl(userProjection.getImage(), 500, 500);
        }

        return UserResponse.builder()
                .id(userProjection.getId())
                .name(userProjection.getName())
                .username(userProjection.getUsername())
                .joinedAt(userProjection.getJoined_At())
                .image(imgPath)
                .chatUUID(chatUUID)
                .build();
    }

    public UserResponse toResponse(final UserEntity user) {
        return toResponse(user, null);
    }


    public UserResponse toResponse(final UserEntity user, final UUID chatUUID) {
        String imgPath = null;

        if (!user.getImage().isEmpty()) {
            imgPath = fileGetService.getFileAbsoluteUrl(user.getImage(), 500, 500);
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .joinedAt(user.getJoinedAt())
                .image(imgPath)
                .chatUUID(chatUUID)
                .build();
    }
}
