package com.campus.service;

import com.campus.model.User;
import com.campus.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Optional<User> getUserById(Integer id) {
    return userRepository.findById(id);
  }

  @Override
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public User updateUser(User user) {
    // Check if user exists
    Optional<User> existingUser = userRepository.findById(user.getUserId());
    if (!existingUser.isPresent()) {
      throw new RuntimeException("User not found");
    }

    // Don't update password through this method
    user.setPassword(existingUser.get().getPassword());

    return userRepository.update(user);
  }

  @Override
  public boolean deleteUser(Integer id) {
    return userRepository.deleteById(id);
  }

  @Override
  public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
    // Find user
    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      return false;
    }

    User user = userOptional.get();

    // Check old password
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      return false;
    }

    // Update password
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.update(user);

    return true;
  }
}