package com.msd.chat.exception;

public class ResourceNotFoundException
        extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
