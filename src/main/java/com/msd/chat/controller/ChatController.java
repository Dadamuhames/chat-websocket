package com.msd.chat.controller;


import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.request.PrivateChatCreateRequest;
import com.msd.chat.model.response.ChatDetailResponse;
import com.msd.chat.model.response.ChatResponse;
import com.msd.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;


    @GetMapping({"", "/"})
    public ResponseEntity<Page<ChatResponse>> list(@AuthenticationPrincipal UserEntity user,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<ChatResponse> chats = chatService.list(user, pageable);

        return ResponseEntity.ok(chats);
    }


    @GetMapping("/{uuid}")
    public ResponseEntity<ChatDetailResponse> one(@PathVariable("uuid") UUID uuid,
                                                  @AuthenticationPrincipal UserEntity user) {

        ChatDetailResponse response = chatService.onePrivate(uuid, user);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/createPrivate")
    public ResponseEntity<?> createPrivate(@Valid @RequestBody PrivateChatCreateRequest request,
                                    @AuthenticationPrincipal UserEntity user) {

        ChatDetailResponse response = chatService.createPrivate(request, user);

        return ResponseEntity.status(201).body(response);
    }
}
