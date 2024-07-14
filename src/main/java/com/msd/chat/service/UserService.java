package com.msd.chat.service;

import com.msd.chat.domain.UserEntity;
import com.msd.chat.exception.ResourceNotFoundException;
import com.msd.chat.mapper.UserMapper;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.model.response.UserResponseInterface;
import com.msd.chat.repository.ChatRepository;
import com.msd.chat.repository.UserRepository;
import com.msd.chat.repository.projection.UserProjection;
import com.msd.chat.utiles.UserSearchType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final ChatRepository chatRepository;
  private final UserMapper userMapper;

  public Page<UserResponseInterface> search(
      final String search,
      final UserSearchType type,
      final UserEntity user,
      final Pageable pageable) {

      Page<UserResponseInterface> usersResponse = Page.empty();

      switch (type) {
          case DETAILED -> {
              Page<UserProjection> users = userRepository.search(search, user.getId(), pageable);
              usersResponse = users.map(userMapper::toResponse);
          }

          case SIMPLE -> {
              Page<UserEntity> users = userRepository.searchForSelect2(search, user.getId(), pageable);
              usersResponse = users.map(userMapper::toSelect2Response);
          }
      }

      return usersResponse;
  }

  public UserResponse one(final Long id, final UserEntity authUser) {
    UserEntity user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    UUID chatUUID =
        chatRepository.findChatUUIDByUserIdAndAuthUserAndPrivate(user.getId(), authUser.getId());

    return userMapper.toResponse(user, chatUUID);
  }
}
