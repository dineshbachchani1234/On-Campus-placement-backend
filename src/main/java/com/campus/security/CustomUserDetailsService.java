package com.campus.security;

import com.campus.model.User;
import com.campus.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // In our system, username can be either userId or email
    User user;

    try {
      // Try to parse as userId
      Integer userId = Integer.parseInt(username);
      Optional<User> userOptional = userRepository.findById(userId);

      if (userOptional.isPresent()) {
        user = userOptional.get();
      } else {
        throw new UsernameNotFoundException("User not found with id: " + username);
      }
    } catch (NumberFormatException e) {
      // If not a number, treat as email
      Optional<User> userOptional = userRepository.findByEmail(username);

      if (userOptional.isPresent()) {
        user = userOptional.get();
      } else {
        throw new UsernameNotFoundException("User not found with email: " + username);
      }
    }

    return new org.springframework.security.core.userdetails.User(
        user.getUserId().toString(),
        user.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    );
  }
}