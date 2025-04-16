package com.campus.repository;
import com.campus.model.User;
import java.util.Optional;

public interface UserRepository {
  void save(User user);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String username);

}