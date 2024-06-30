package com.msd.chat.controller;

import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.request.LoginRequest;
import com.msd.chat.model.request.RefreshTokenRequest;
import com.msd.chat.model.request.SignUpRequest;
import com.msd.chat.model.response.TokensResponse;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<TokensResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        TokensResponse tokens = authService.signUp(request);
        return ResponseEntity.status(201).body(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<TokensResponse> login(@Valid @RequestBody LoginRequest request) {
        TokensResponse tokens = authService.login(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokensResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokensResponse tokens = authService.refresh(request);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> profile(@AuthenticationPrincipal UserEntity user) {
        UserResponse response = authService.profile(user);
        return ResponseEntity.ok(response);
    }
}
