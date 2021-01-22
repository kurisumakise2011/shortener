package com.edu.exception;

public class UniqueViolationException extends RuntimeException {
  public UniqueViolationException() {
  }

  public UniqueViolationException(String message) {
    super(message);
  }

  public UniqueViolationException(String message, Throwable cause) {
    super(message, cause);
  }

  public UniqueViolationException(Throwable cause) {
    super(cause);
  }
}
