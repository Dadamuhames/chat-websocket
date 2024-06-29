package com.msd.chat.service;

import com.msd.chat.domain.RefreshTokenEntity;
import com.msd.chat.exception.BaseException;
import com.msd.chat.exception.UnauthorizedException;
import com.msd.chat.repository.RefreshTokenRepository;
import com.msd.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserDetailService userDetailService;

    public RefreshTokenEntity createRefreshToken(final String username) {
        boolean userExists =
                userRepository.existsByUsername(username);

        if (!userExists) {
            throw new BaseException(Map.of("error", "Refresh token invalid"), 400);
        }

        RefreshTokenEntity refreshToken =
                RefreshTokenEntity.builder()
                        .username(username)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(Instant.now().plusMillis(90 * 1000000))
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenEntity findByToken(final String token) {
        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("token invalid"));
    }

    public UserDetails getUser(final String token) {
        RefreshTokenEntity refreshToken = findByToken(token);

        return userDetailService.loadUserByUsername(refreshToken.getUsername());
    }

    public void verifyExpiration(final RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new UnauthorizedException("Refresh token is expired.");
        }
    }

    public void deleteToken(final RefreshTokenEntity token) {
        refreshTokenRepository.delete(token);
    }

    public void deleteByToken(final String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
}
