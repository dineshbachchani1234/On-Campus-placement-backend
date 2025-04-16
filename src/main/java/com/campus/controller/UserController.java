package com.campus.controller;


import com.campus.model.MessageResponse;
import com.campus.model.User;
import com.campus.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for user operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  /**
   * Get user by ID
   * @param id The user ID
   * @return User if found
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable Integer id) {
    Optional<User> user = userService.getUserById(id);

    if (user.isPresent()) {
      return ResponseEntity.ok(user.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get all users
   * @return List of all users
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  /**
   * Update user
   * @param id The user ID
   * @param user The updated user data
   * @return Updated user
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
    // Ensure ID consistency
    if (!id.equals(user.getUserId())) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error("ID in path variable doesn't match ID in request body"));
    }

    try {
      User updatedUser = userService.updateUser(user);
      return ResponseEntity.ok(updatedUser);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  /**
   * Delete user
   * @param id The user ID
   * @return Success message
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
    boolean deleted = userService.deleteUser(id);

    if (deleted) {
      return ResponseEntity.ok(MessageResponse.success("User deleted successfully"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Change password
   * @param id The user ID
   * @param oldPassword The old password
   * @param newPassword The new password
   * @return Success message
   */
  @PostMapping("/{id}/change-password")
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
  public ResponseEntity<?> changePassword(
      @PathVariable Integer id,
      @RequestParam String oldPassword,
      @RequestParam String newPassword) {

    boolean changed = userService.changePassword(id, oldPassword, newPassword);

    if (changed) {
      return ResponseEntity.ok(MessageResponse.success("Password changed successfully"));
    } else {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error("Invalid old password"));
    }
  }
}