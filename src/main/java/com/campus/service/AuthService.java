package com.campus.service;

import com.campus.model.JwtAuthResponse;
import com.campus.model.LoginRequest;
import com.campus.model.SignupRequest;
import com.campus.model.User;

/**
 * Service for authentication operations
 */
public interface AuthService {

  /**
   * Authenticate a user and generate a JWT token
   * @param loginRequest The login request
   * @return JWT authentication response
   */
  JwtAuthResponse login(LoginRequest loginRequest);

  /**
   * Register a new user
   * @param signupRequest The signup request
   * @return The newly created user
   */
  User register(SignupRequest signupRequest);

  /**
   * Check if an email already exists
   * @param email The email to check
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);
}