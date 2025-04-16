package com.campus.repository;

import com.campus.model.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer> {
  /**
   * Find a user by email
   * @param email The email to search for
   * @return Optional containing the user if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Check if an email already exists
   * @param email The email to check
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);
}