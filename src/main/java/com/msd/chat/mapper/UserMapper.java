package com.msd.chat.mapper;

import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.request.ProfileEditRequest;
import com.msd.chat.model.request.SignUpRequest;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.repository.projection.UserProjection;
import com.msd.chat.service.file.FileGetService;
import com.msd.chat.utiles.UUIDConverter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
  private final FileGetService fileGetService;
  private final PasswordEncoder passwordEncoder;
  private final UUIDConverter uuidConverter;

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


  public UserEntity fromProfileUpdateRequest(final ProfileEditRequest request, final UserEntity user) {
    String image = request.image() == null ? user.getImage() : request.image();

    System.out.println(user.getPassword());

    return UserEntity.builder()
            .id(user.getId())
            .username(request.username())
            .name(request.name())
            .password(user.getPassword())
            .active(true)
            .image(image)
            .build();
  }


  public UserResponse toResponse(final UserProjection userProjection) {
    String imgPath = fileGetService.getFileAbsoluteUrl(userProjection.getImage(), 500, 500);

    UUID uuid =
        userProjection.getChatUUID() != null
            ? uuidConverter.getUUIDFromBytes(userProjection.getChatUUID())
            : null;

    return UserResponse.builder()
        .id(userProjection.getId())
        .name(userProjection.getName())
        .username(userProjection.getUsername())
        .joinedAt(userProjection.getJoined_At())
        .image(imgPath)
        .chatUUID(uuid)
        .build();
  }

  public UserResponse toResponse(final UserEntity user) {
    return toResponse(user, null);
  }

  public UserResponse toResponse(final UserEntity user, final UUID chatUUID) {
    String imgPath = fileGetService.getFileAbsoluteUrl(user.getImage(), 500, 500);

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
