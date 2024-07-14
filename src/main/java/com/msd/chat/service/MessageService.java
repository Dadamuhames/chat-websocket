package com.msd.chat.service;

import com.msd.chat.domain.ChatEntity;
import com.msd.chat.domain.MessageEntity;
import com.msd.chat.domain.UserEntity;
import com.msd.chat.domain.enums.ChatTypes;
import com.msd.chat.exception.BaseException;
import com.msd.chat.exception.ResourceNotFoundException;
import com.msd.chat.mapper.MessageMapper;
import com.msd.chat.model.request.MessageCreateRequest;
import com.msd.chat.model.response.MessageReadResponse;
import com.msd.chat.model.response.MessageResponse;
import com.msd.chat.model.response.MessageSocketResponse;
import com.msd.chat.repository.ChatRepository;
import com.msd.chat.repository.MessageRepository;
import com.msd.chat.repository.UserRepository;

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
public class MessageService {
  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final MessageMapper messageMapper;
  private final SimpMessagingTemplate messagingTemplate;

  public void sendOnRead(final String username, final List<UUID> uuids, final UUID chatUUID) {
    MessageReadResponse response =
        MessageReadResponse.builder().messagesUUID(uuids).chatUUID(chatUUID).build();

    messagingTemplate.convertAndSendToUser(username, "/messages/read", response);
  }

  public void readMessage(final MessageEntity message, final UserEntity user) {
    Set<UserEntity> users =
        message.getReadByUsers() == null ? new HashSet<>() : message.getReadByUsers();
    users.add(user);
    message.setReadByUsers(users);
  }

  @Transactional
  public void readNewMessages(final UUID chatUUID, final UserEntity user) {
    List<MessageEntity> messages = messageRepository.findNewForUserByChatId(chatUUID, user.getId());

    Map<String, List<UUID>> mapOfUsers = new HashMap<>();

    for (MessageEntity message : messages) {
      readMessage(message, user);

      // set uuids in MAP for send to websocket
      String username = message.getFromUser().getUsername();
      List<UUID> uuids = mapOfUsers.getOrDefault(username, new ArrayList<>());
      uuids.add(message.getUuid());
      mapOfUsers.put(username, uuids);
    }

    messageRepository.saveAll(messages);

    for (Map.Entry<String, List<UUID>> entry : mapOfUsers.entrySet()) {
      sendOnRead(entry.getKey(), entry.getValue(), chatUUID);
    }
  }

  public void readMessageByUUID(final UUID uuid, final UserEntity user) {
    MessageEntity message = messageRepository.findByUUIDAndUserId(uuid, user.getId()).orElse(null);

    if (message != null) {
      readMessage(message, user);
      messageRepository.save(message);

      sendOnRead(
          message.getFromUser().getUsername(),
          List.of(message.getUuid()),
          message.getChat().getUuid());
    }
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
            .findByUUIDAndUserId(request.chatUUID(), user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

    boolean newChat = false;

    if (!chat.isActive()) {
      chat.setActive(true);
      newChat = true;
    }

    chat.setLastMessageAt(LocalDateTime.now());
    chat = chatRepository.save(chat);

    MessageEntity message =
        MessageEntity.builder().message(request.message()).fromUser(user).chat(chat).build();

    message = messageRepository.saveAndFlush(message);

    Long newMessagesCount = messageRepository.countNewMessagedByChatAndUser(request.chatUUID(), user.getId());

    MessageSocketResponse socketResponse = messageMapper.toSocketResponse(message, newMessagesCount, newChat);

    Set<UserEntity> companions = chat.getUsers();

    for(UserEntity companion : companions) {
      if(!companion.getId().equals(user.getId())) {
        messagingTemplate.convertAndSendToUser(
                companion.getUsername(), "/messages", socketResponse);
      }
    }

    return messageMapper.toResponse(message, user);
  }
}
