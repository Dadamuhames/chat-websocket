package com.msd.chat.service;


import com.msd.chat.domain.UserEntity;
import com.msd.chat.exception.ResourceNotFoundException;
import com.msd.chat.mapper.UserMapper;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserResponse> search(final String search, final UserDetails userDetails, final Pageable pageable) {
        UserEntity user = (UserEntity) userDetails;

        Page<UserEntity> users = userRepository.search(search, user.getId(), pageable);

        return users.map(userMapper::toResponse);
    }

    public UserResponse one(final Long id, final UserDetails userDetails) {
//        UserEntity currentUser = (UserEntity) userDetails;

        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        return userMapper.toResponse(user);
    }
}
