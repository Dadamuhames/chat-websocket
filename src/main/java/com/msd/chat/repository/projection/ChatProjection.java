package com.msd.chat.repository.projection;

import com.msd.chat.domain.enums.ChatTypes;
import java.util.UUID;

public interface ChatProjection {
  Long getId();

  byte[] getUUID();

  String getName();

  ChatTypes getType();

  String getImage();

  String getUserFullName();

  String getUsername();

  Long getUserId();

  String getUserImage();

  Long getNewMessagesCount();
}
