package com.campus.model;

public class JwtAuthResponse {
  private String accessToken;
  private String tokenType = "Bearer";
  private Integer userId;
  private String email;
  private User.Role role;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public User.Role getRole() {
    return role;
  }

  public void setRole(User.Role role) {
    this.role = role;
  }

  public JwtAuthResponse(String accessToken, Integer userId, String email, User.Role role) {
    this.accessToken = accessToken;
    this.userId = userId;
    this.email = email;
    this.role = role;
  }
}