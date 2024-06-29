package com.msd.chat.mapper;


import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.request.SignUpRequest;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.service.file.FileGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final FileGetService fileGetService;

    public UserEntity fromSignUpRequest(final SignUpRequest signUpRequest) {
        return UserEntity.builder()
                .username(signUpRequest.username())
                .name(signUpRequest.name())
                .password(signUpRequest.password())
                .image(signUpRequest.image())
                .active(true)
                .build();
    }


    public UserResponse toResponse(final UserEntity user) {
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
                .build();
    }
}
