package com.msd.chat.exception;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;


public class WebsocketException extends BaseException {
    public WebsocketException(Map<String, String> errors, int status) {
        super(errors, status);
    }
}
