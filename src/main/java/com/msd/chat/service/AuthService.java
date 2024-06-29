package com.msd.chat.service;


import com.msd.chat.domain.UserEntity;
import com.msd.chat.exception.BaseException;
import com.msd.chat.mapper.UserMapper;
import com.msd.chat.model.request.LoginRequest;
import com.msd.chat.model.request.SignUpRequest;
import com.msd.chat.model.response.TokensResponse;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.repository.UserRepository;
import com.msd.chat.service.file.FileStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final GeneralAuthService generalAuthService;
    private final AuthenticationManager authenticationManager;
    private final FileStoreService fileStoreService;
    private final UserMapper userMapper;


    // sign up
    public TokensResponse signUp(final SignUpRequest request) {
        Boolean usernameExists = userRepository.existsByUsername(request.username());

        if (usernameExists) {
            throw new BaseException(Map.of("error", "Username already in use"), 403);
        }

        UserEntity user = userMapper.fromSignUpRequest(request);
        user = userRepository.save(user);
        fileStoreService.deleteByFile(user.getImage());

        return generalAuthService.createPair(user.getUsername());
    }


    // login
    public TokensResponse login(final LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(), request.password()
        ));

        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Username or password invalid");
        }

        return generalAuthService.createPair(request.username());
    }


    // profile
    public UserResponse profile(final UserDetails userDetails) {
        UserEntity user = (UserEntity) userDetails;

        return userMapper.toResponse(user);
    }
}
