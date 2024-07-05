package com.msd.chat.service;

import com.msd.chat.domain.ChatEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.domain.enums.ChatTypes;
import com.msd.chat.exception.BaseException;
import com.msd.chat.exception.ResourceNotFoundException;
import com.msd.chat.mapper.ChatMapper;
import com.msd.chat.model.request.PrivateChatCreateRequest;
import com.msd.chat.model.response.ChatDetailResponse;
import com.msd.chat.model.response.ChatResponse;
import com.msd.chat.repository.ChatRepository;
import com.msd.chat.repository.UserRepository;
import com.msd.chat.repository.projection.ChatProjection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepository chatRepository;
  private final UserRepository userRepository;
  private final ChatMapper chatMapper;
  private final MessageService messageService;

  public Page<ChatResponse> list(final UserEntity user, final Pageable pageable) {
    Page<ChatProjection> chats = chatRepository.findByUserId(user.getId(), pageable);

    return chats.map(chatMapper::toResponse);
  }

  public ChatDetailResponse onePrivate(final UUID uuid, final UserEntity user) {
    ChatEntity chat =
        chatRepository
            .findByUUIDAndUserId(uuid, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

    UserEntity companion = userRepository.findCompanionByChat(chat.getId(), user.getId());

    messageService.readNewMessages(chat.getId(), user);

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
}
