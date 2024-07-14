package com.msd.chat.service;


import com.msd.chat.domain.RefreshTokenEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.exception.BaseException;
import com.msd.chat.mapper.UserMapper;
import com.msd.chat.model.request.*;
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
    private final AuthenticationManager authenticationManager;
    private final FileStoreService fileStoreService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserMapper userMapper;


    public TokensResponse createPair(final String username) {
        RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(username);
        String accessToken = jwtService.generateToken(username);

        return TokensResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    // sign up
    public TokensResponse signUp(final SignUpRequest request) {
        Boolean usernameExists = userRepository.existsByUsername(request.username());

        if (usernameExists) {
            throw new BaseException(Map.of("error", "Username already in use"), 403);
        }

        UserEntity user = userMapper.fromSignUpRequest(request);
        user = userRepository.save(user);
        fileStoreService.deleteByFile(user.getImage());

        return createPair(user.getUsername());
    }


    // login
    public TokensResponse login(final LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(), request.password()
        ));

        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Username or password invalid");
        }

        return createPair(request.username());
    }

    // profile
    public UserResponse profile(final UserEntity user) {
        return userMapper.toResponse(user);
    }


    // profile update
    public UserResponse profileUpdate(final ProfileEditRequest request, final UserEntity user) {
        Boolean usernameExists = userRepository.existsByUsernameAndIdNot(request.username(), user.getId());

        if (usernameExists) {
            throw new BaseException(Map.of("error", "Username already in use"), 403);
        }

        UserEntity updatedUser = userMapper.fromProfileUpdateRequest(request, user);

        updatedUser = userRepository.save(updatedUser);

        if(request.image() != null && !request.image().isEmpty()) {
            fileStoreService.deleteByFile(request.image());
        }

        return userMapper.toResponse(updatedUser);
    }


    // token refresh
    public TokensResponse refresh(final RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.refreshToken();

        RefreshTokenEntity refreshToken = refreshTokenService.findByToken(token);

        refreshTokenService.verifyExpiration(refreshToken);

        UserDetails user = refreshTokenService.getUser(token);

        // delete old token
        refreshTokenService.deleteToken(refreshToken);

        return createPair(user.getUsername());
    }

    public void logout(final LogoutRequest refreshTokenRequest) {
        refreshTokenService.deleteByToken(refreshTokenRequest.refreshToken());
    }
}
