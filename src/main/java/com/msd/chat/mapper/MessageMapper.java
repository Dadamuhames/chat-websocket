package com.msd.chat.mapper;

import com.msd.chat.domain.ChatEntity;
import com.msd.chat.domain.MessageEntity;
import com.msd.chat.model.response.ChatDetailResponse;
import com.msd.chat.model.response.ChatResponse;
import com.msd.chat.model.response.MessageResponse;
import com.msd.chat.model.response.MessageSocketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final ChatMapper chatMapper;

    public MessageResponse toResponse(final MessageEntity message) {
        return MessageResponse.builder()
                .id(message.getId())
                .uuid(message.getUuid())
                .chatUUID(message.getChat().getUuid())
                .message(message.getMessage())
                .fromUserId(message.getFromUser().getId())
                .createdAt(message.getCreatedAt())
                .build();
    }


    public MessageSocketResponse toSocketResponse(final MessageEntity message, final Boolean isChatNew) {
        ChatResponse chat = chatMapper.toResponse(message.getChat(), message.getFromUser());

        return MessageSocketResponse.builder()
                .id(message.getId())
                .uuid(message.getUuid())
                .message(message.getMessage())
                .isChatNew(isChatNew)
                .chat(chat)
                .build();
    }
}
