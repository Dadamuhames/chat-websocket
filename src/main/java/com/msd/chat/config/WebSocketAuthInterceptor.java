package com.msd.chat.config;

import com.msd.chat.exception.WebsocketException;
import com.msd.chat.service.JwtService;
import com.msd.chat.service.UserDetailService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
  private final JwtService jwtService;
  private final UserDetailService userDetailService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

//
//      if(StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor) {
//
//      }

    assert accessor != null;
    String authHeader = accessor.getFirstNativeHeader("Authorization");

    if (authHeader == null || authHeader.isEmpty())
      throw new WebsocketException(Map.of("error", "Token invalid"), 401);

    if (StompCommand.CONNECT.equals(accessor.getCommand())
        || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      final String jwt;
      final String username;

      jwt = authHeader.substring(7);
      username = jwtService.extractUsername(jwt);
      boolean tokenInvalid = true;

      if (username != null) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());

          accessor.setUser(authenticationToken);
          tokenInvalid = false;
        }
      }

//      if(tokenInvalid) {
//          throw new WebsocketException(Map.of("error", "Token invalid"), 401);
//      }
    }

    return message;
  }
}
