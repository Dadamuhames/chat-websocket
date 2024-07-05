package com.msd.chat.controller;

import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.request.MessageCreateRequest;
import com.msd.chat.model.response.MessageResponse;
import com.msd.chat.service.MessageService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
  private final MessageService messageService;

  @GetMapping("/{chatUUID}")
  public ResponseEntity<Page<MessageResponse>> chatMessagesList(
      @PathVariable("chatUUID") UUID uuid,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int pageSize,
      @AuthenticationPrincipal UserEntity user) {

    Pageable pageable = PageRequest.of(page, pageSize);

    Page<MessageResponse> messages = messageService.getChatMessages(uuid, user, pageable);

    return ResponseEntity.ok(messages);
  }

  @PostMapping({"", "/"})
  public ResponseEntity<MessageResponse> create(
      @Valid @RequestBody MessageCreateRequest request, @AuthenticationPrincipal UserEntity user) {

    MessageResponse response = messageService.create(request, user);

    return ResponseEntity.status(201).body(response);
  }
}
