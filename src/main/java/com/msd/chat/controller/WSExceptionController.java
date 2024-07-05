package com.msd.chat.controller;

import com.msd.chat.exception.BaseException;
import com.msd.chat.exception.WebsocketException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WSExceptionController {
    @MessageExceptionHandler(WebsocketException.class)
    @SendToUser("/errors")
    public Map<String, String> handleBaseException(WebsocketException e) {
        return e.getErrors();
    }
}
