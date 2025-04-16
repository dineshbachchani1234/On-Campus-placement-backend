package com.campus.model;

public class MessageResponse {
  private String message;
  private boolean success;

  public MessageResponse(String message, boolean b) {
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public static MessageResponse success(String message) {
    return new MessageResponse(message, true);
  }

  public static MessageResponse error(String message) {
    return new MessageResponse(message, false);
  }
}