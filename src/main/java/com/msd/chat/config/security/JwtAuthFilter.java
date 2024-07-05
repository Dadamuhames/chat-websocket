package com.msd.chat.config.security;

import com.msd.chat.service.JwtService;
import com.msd.chat.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailService userDetailService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      jwt = authHeader.substring(7);
      username = jwtService.extractUsername(jwt);

      if (!jwtService.validateJwtToken(jwt)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalid");
      }

      if (username != null
          && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());

          authenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));


          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }
    } catch (Exception ex) {
      log.error("JWT filter error: {}", ex.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
