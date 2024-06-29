package com.msd.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// exception handler
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleResourceNotFoundExceptionForApi(
      final ResourceNotFoundException ex) {
    Map<String, String> errorMessage = new HashMap<>();

    errorMessage.put("error", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Map<String, String>> handleUnauthorizedException(
      final UnauthorizedException ex) {
    Map<String, String> errorMessage = new HashMap<>();

    errorMessage.put("error", ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodValidationExceptions(
      final MethodArgumentNotValidException ex) {
    BindingResult bindingResult = ex.getBindingResult();
    Map<String, String> errors = new HashMap<>();

    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return ResponseEntity.status(403).body(Map.of("error", errors));
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(
      final BaseException ex) {
    return ResponseEntity.status(ex.getStatus()).body(ex.getErrors());
  }
}
