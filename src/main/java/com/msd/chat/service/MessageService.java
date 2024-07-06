package com.msd.chat.service;

import com.msd.chat.domain.ChatEntity;
import com.msd.chat.domain.MessageEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.exception.BaseException;
import com.msd.chat.exception.ResourceNotFoundException;
import com.msd.chat.mapper.MessageMapper;
import com.msd.chat.model.request.MessageCreateRequest;
import com.msd.chat.model.response.MessageResponse;
import com.msd.chat.model.response.MessageSocketResponse;
import com.msd.chat.repository.ChatRepository;
import com.msd.chat.repository.MessageRepository;
import com.msd.chat.repository.UserRepository;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final MessageMapper messageMapper;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;


  @Transactional
  public void readNewMessages(final UUID chatUUID, final UserEntity user) {
    List<MessageEntity> messages = messageRepository.findNewForUserByChatId(chatUUID, user.getId());

    for(MessageEntity message : messages) {
      Set<UserEntity> users = message.getReadByUsers() == null ? new HashSet<>() : message.getReadByUsers();
      users.add(user);
      message.setReadByUsers(users);
    }

    messageRepository.saveAll(messages);
  }


  public Page<MessageResponse> getChatMessages(
      final UUID chatUUID, final UserEntity user, final Pageable pageable) {
    Boolean chatExists = chatRepository.existsByUserIdAndUUID(chatUUID, user.getId());

    if (!chatExists) {
      throw new ResourceNotFoundException("Chat UUID invalid");
    }

    Page<MessageEntity> messages = messageRepository.findByChatUuid(chatUUID, pageable);

    try {
      readNewMessages(chatUUID, user);
    } catch (Exception e) {
      throw new BaseException(Map.of("message_change_error", e.getMessage()), 403);
    }

    return messages.map(message -> messageMapper.toResponse(message, user));
  }


  @Transactional
  public MessageResponse create(final MessageCreateRequest request, final UserEntity user) {
    ChatEntity chat =
        chatRepository
            .findByUserIdAndUUID(request.chatUUID(), user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

    UserEntity companionUser = userRepository.findCompanionByChat(chat.getId(), user.getId());

    boolean newChat = false;

    if (!chat.isActive()) {
      chat.setActive(true);
      chat = chatRepository.save(chat);
      newChat = true;
    }

    MessageEntity message =
        MessageEntity.builder().message(request.message()).fromUser(user).chat(chat).build();

    message = messageRepository.saveAndFlush(message);

    MessageSocketResponse socketResponse = messageMapper.toSocketResponse(message, newChat);

    messagingTemplate.convertAndSendToUser(
        companionUser.getUsername(), "/messages", socketResponse);

    return messageMapper.toResponse(message, user);
  }

//  public MessageResponse create(final MessageCreateRequest request, final UserEntity user) {
//    MessageEntity message = createMessage(request, user);
//
//    return messageMapper.toResponse(message, user);
//  }
}
