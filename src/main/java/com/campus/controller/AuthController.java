package com.campus.controller;

import com.campus.model.JwtAuthResponse;
import com.campus.model.LoginRequest;
import com.campus.model.SignupRequest;
import com.campus.model.MessageResponse;
import com.campus.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controller for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

  @Autowired
  private AuthService authService;

  /**
   * Login endpoint
   * @param loginRequest The login request
   * @return JWT authentication response
   */
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser( @RequestBody LoginRequest loginRequest) {
    try {
      JwtAuthResponse response = authService.login(loginRequest);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(MessageResponse.error("Invalid username or password"));
    }
  }

  /**
   * Signup endpoint
   * @param signupRequest The signup request
   * @return Success message
   */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser( @RequestBody SignupRequest signupRequest) {
    try {
      // Check if email exists
      if (authService.existsByEmail(signupRequest.getEmail())) {
        return ResponseEntity.badRequest()
            .body(MessageResponse.error("Email is already taken"));
      }

      // Register user
      authService.register(signupRequest);

      return ResponseEntity.ok(MessageResponse.success("User registered successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  /**
   * Check if email exists
   * @param email The email to check
   * @return Boolean indicating if email exists
   */
  @GetMapping("/check-email")
  public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
    boolean exists = authService.existsByEmail(email);
    return ResponseEntity.ok(exists);
  }
}