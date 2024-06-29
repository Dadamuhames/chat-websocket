package com.msd.chat.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BaseException extends RuntimeException {
  public int status;
  public Map<String, String> errors;

  public BaseException(Map<String, String> errors, int status) {
    this.status = status;
    this.errors = errors;
  }
}
