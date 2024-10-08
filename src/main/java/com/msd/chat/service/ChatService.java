package com.msd.chat.service;

import com.msd.chat.domain.ChatEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.domain.enums.ChatTypes;
import com.msd.chat.exception.BaseException;
import com.msd.chat.exception.ResourceNotFoundException;
import com.msd.chat.mapper.ChatMapper;
import com.msd.chat.model.request.GroupChatCreateRequest;
import com.msd.chat.model.request.PrivateChatCreateRequest;
import com.msd.chat.model.response.ChatDetailResponse;
import com.msd.chat.model.response.ChatResponse;
import com.msd.chat.repository.ChatRepository;
import com.msd.chat.repository.UserRepository;
import com.msd.chat.repository.projection.ChatProjection;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepository chatRepository;
  private final UserRepository userRepository;
  private final ChatMapper chatMapper;
  private final SimpMessagingTemplate messagingTemplate;

  public Page<ChatResponse> list(final UserEntity user, final Pageable pageable) {
    Page<ChatProjection> chats = chatRepository.findByUserId(user.getId(), pageable);

    return chats.map(chatMapper::toResponse);
  }

  public ChatDetailResponse one(final UUID uuid, final UserEntity user) {
    ChatEntity chat =
        chatRepository
            .findByUUIDAndUserId(uuid, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

    // group chat
    if (chat.getType() == ChatTypes.GROUP) return chatMapper.toResponseGroup(chat);

    // private chat
    UserEntity companion = userRepository.findCompanionByChat(chat.getId(), user.getId());
    return chatMapper.toResponsePrivate(chat, companion);
  }

  @Transactional
  public ChatDetailResponse createPrivate(
      final PrivateChatCreateRequest request, final UserEntity user) {

    Boolean chatExists =
        chatRepository.existsByUserIdAndAuthUserAndPrivate(request.userId(), user.getId());

    if (chatExists) {
      throw new BaseException(Map.of("error", "Chat with this user already exists"), 403);
    }

    UserEntity userToChatWith =
        userRepository
            .findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    ChatEntity chat = ChatEntity.builder().type(ChatTypes.PRIVATE).active(false).build();

    chat = chatRepository.save(chat);

    Set<UserEntity> users = new HashSet<>();

    users.add(user);
    users.add(userToChatWith);

    chat.setUsers(users);

    chatRepository.save(chat);

    return chatMapper.toResponsePrivate(chat, userToChatWith);
  }

  @Transactional
  public ChatEntity createGroup(final GroupChatCreateRequest request, final UserEntity user) {
    Set<UserEntity> users = new HashSet<>(userRepository.findAllById(request.usersId()));

    if (users.isEmpty()) {
      throw new BaseException(Map.of("error", "Users count invalid"), 403);
    }

    users.add(user);

    ChatEntity chat =
        ChatEntity.builder()
            .name(request.name())
            .image(request.image())
            .type(ChatTypes.GROUP)
            .active(true)
            .admin(user)
            .lastMessageAt(LocalDateTime.now())
            .build();

    chat = chatRepository.save(chat);
    chat.setUsers(users);
    chat = chatRepository.save(chat);

    return chat;
  }

  public ChatDetailResponse createGroupAndSend(
      final GroupChatCreateRequest request, final UserEntity user) {

    ChatEntity chat = createGroup(request, user);

    ChatDetailResponse response = chatMapper.toResponseGroup(chat);

    for (UserEntity userEntity : chat.getUsers()) {
      if (!userEntity.getId().equals(user.getId())) {
        messagingTemplate.convertAndSendToUser(
            userEntity.getUsername(), "/groups/created", response);
      }
    }

    return response;
  }
}
