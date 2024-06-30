package com.msd.chat.service;


import com.msd.chat.domain.UserEntity;
import com.msd.chat.exception.ResourceNotFoundException;
import com.msd.chat.mapper.UserMapper;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.repository.ChatRepository;
import com.msd.chat.repository.UserRepository;
import com.msd.chat.repository.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserMapper userMapper;

    public Page<UserResponse> search(final String search, final UserEntity user, final Pageable pageable) {
        Page<UserProjection> users = userRepository.search(search, user.getId(), pageable);

        return users.map(userMapper::toResponse);
    }

    public UserResponse one(final Long id, final UserEntity authUser) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        UUID chatUUID = chatRepository.findChatUUIDByUserIdAndAuthUserAndPrivate(user.getId(), authUser.getId());

        return userMapper.toResponse(user, chatUUID);
    }
}
