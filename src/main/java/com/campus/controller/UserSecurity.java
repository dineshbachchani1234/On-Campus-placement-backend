package com.campus.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security helper for user operations
 */
@Component
public class UserSecurity {

  /**
   * Check if the authenticated user is the requested user
   * @param userId The user ID to check
   * @return true if current user, false otherwise
   */
  public boolean isCurrentUser(Integer userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof org.springframework.security.core.userdetails.User) {
      String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
      // Username is the user ID in our case
      return username.equals(userId.toString());
    }

    return false;
  }
}