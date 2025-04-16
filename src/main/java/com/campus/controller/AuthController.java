package com.campus.controller;

import com.campus.model.JwtAuthResponse;
import com.campus.model.LoginRequest;
import com.campus.model.MessageResponse;
import com.campus.model.SignupRequest;
import com.campus.model.User;
import com.campus.repository.UserRepository;
import com.campus.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    // Authenticate user
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        )
    );

    // Set authentication in security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate JWT token
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String jwt = jwtTokenProvider.generateToken(userDetails);

    // Find user details
    User user = userRepository.findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Return JWT and user details in response
    return ResponseEntity.ok(new JwtAuthResponse(
        jwt,
        user.getUserId(),
        user.getUsername(),
        user.getFirstName(),
        user.getLastName(),
        user.getRole()
    ));
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
    // Check if email already exists
    if (userRepository.existsByEmail(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse());
    }

    User user = new User(
        signUpRequest.getId(),
        signUpRequest.getUsername(),
        passwordEncoder.encode(signUpRequest.getPassword()),
        signUpRequest.getRole(),
        signUpRequest.getFirst_name(),
        signUpRequest.getLast_name()
    );

    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse());
  }
}