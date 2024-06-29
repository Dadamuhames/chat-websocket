package com.msd.chat.service;

import com.msd.chat.domain.RefreshTokenEntity;
import com.msd.chat.model.request.LogoutRequest;
import com.msd.chat.model.request.RefreshTokenRequest;
import com.msd.chat.model.response.TokensResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralAuthService {
  private final RefreshTokenService refreshTokenService;
  private final JwtService jwtService;

  public TokensResponse createPair(final String username) {
    RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(username);
    String accessToken = jwtService.generateToken(username);

    return TokensResponse.builder()
        .accessToken(accessToken)
        .refreshToken(newRefreshToken.getToken())
        .build();
  }

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
