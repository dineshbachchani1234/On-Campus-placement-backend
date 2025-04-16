package com.campus.service;

import com.campus.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service for user operations
 */
public interface UserService {

  /**
   * Get user by ID
   * @param id The user ID
   * @return Optional containing the user if found
   */
  Optional<User> getUserById(Integer id);

  /**
   * Get user by email
   * @param email The email
   * @return Optional containing the user if found
   */
  Optional<User> getUserByEmail(String email);

  /**
   * Get all users
   * @return List of all users
   */
  List<User> getAllUsers();

  /**
   * Update user
   * @param user The user to update
   * @return The updated user
   */
  User updateUser(User user);

  /**
   * Delete user by ID
   * @param id The user ID
   * @return true if deleted, false otherwise
   */
  boolean deleteUser(Integer id);

  /**
   * Change user password
   * @param userId The user ID
   * @param oldPassword The old password
   * @param newPassword The new password
   * @return true if changed, false otherwise
   */
  boolean changePassword(Integer userId, String oldPassword, String newPassword);
}