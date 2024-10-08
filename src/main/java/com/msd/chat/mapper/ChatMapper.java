package com.msd.chat.mapper;

import com.msd.chat.domain.ChatEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.response.ChatDetailResponse;
import com.msd.chat.model.response.ChatResponse;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.repository.projection.ChatProjection;
import com.msd.chat.service.file.FileGetService;
import com.msd.chat.utiles.UUIDConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMapper {
  private final FileGetService fileGetService;
  private final UUIDConverter uuidConverter;
  private final UserMapper userMapper;

  public ChatDetailResponse toResponsePrivate(final ChatEntity chat, final UserEntity user) {
    String imagePath = fileGetService.getFileAbsoluteUrl(user.getImage(), 500, 500);

    UserResponse response = userMapper.toResponse(user);

    return ChatDetailResponse.builder()
        .id(chat.getId())
        .uuid(chat.getUuid())
        .name(user.getName())
        .image(imagePath)
        .user(response)
        .build();
  }


  public ChatDetailResponse toResponseGroup(final ChatEntity chat) {
    String imagePath = fileGetService.getFileAbsoluteUrl(chat.getImage(), 500, 500);

    return ChatDetailResponse.builder()
            .id(chat.getId())
            .uuid(chat.getUuid())
            .name(chat.getName())
            .image(imagePath)
            .build();
  }


  public ChatResponse toResponse(final ChatEntity chat, final UserEntity user) {
    String name = chat.getName() != null ? chat.getName() : user.getName();
    String image = chat.getImage() != null ? chat.getImage() : user.getImage();

    String imagePath = fileGetService.getFileAbsoluteUrl(image, 500, 500);

    return ChatResponse.builder()
            .id(chat.getId())
            .uuid(chat.getUuid())
            .name(name)
            .type(chat.getType())
            .username(user.getUsername())
            .image(imagePath)
            .userId(user.getId())
            .build();
  }

  public ChatResponse toResponse(final ChatProjection chat) {
    String name = chat.getName() != null ? chat.getName() : chat.getUserFullName();
    String image = chat.getImage() != null ? chat.getImage() : chat.getUserImage();

    String imagePath = fileGetService.getFileAbsoluteUrl(image, 500, 500);

    return ChatResponse.builder()
            .id(chat.getId())
            .uuid(uuidConverter.getUUIDFromBytes(chat.getUUID()))
            .name(name)
            .type(chat.getType())
            .username(chat.getUsername())
            .image(imagePath)
            .userId(chat.getUserId())
            .newMessagesCount(chat.getNewMessagesCount())
            .lastMessage(chat.getLastMessage())
            .build();
  }
}
