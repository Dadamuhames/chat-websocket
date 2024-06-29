package com.msd.chat.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PaymeException extends RuntimeException {
  public String id;
  public String data;
  public Integer code;
  public Map<String, String> paymeMessage;

  public PaymeException(final String id, final Map<String, String> paymeMessage, final Integer code) {
    this.id = id;
    this.paymeMessage = paymeMessage;
    this.code = code;
  }

  public PaymeException(
      final String id, final Map<String, String> paymeMessage, final Integer code, final String data) {
    this.id = id;
    this.paymeMessage = paymeMessage;
    this.code = code;
    this.data = data;
  }

  public PaymeException(final String id, final Integer code) {
    this.id = id;
    this.code = code;
  }
}
