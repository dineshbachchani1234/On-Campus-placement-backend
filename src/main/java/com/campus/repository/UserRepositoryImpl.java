package com.campus.repository;

import com.campus.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(User user) {
    String sql = "INSERT INTO user (username, password, first_name, last_name, role) VALUES (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getRole());
  }

  @Override
  public Optional<User> findByUsername(String username) {
    String sql = "SELECT * FROM user WHERE username = ?";
    return jdbcTemplate.query(sql, new Object[]{username}, rs -> {
      if (rs.next()) {
        User u = new User();
        //u.setUserId(rs.getInt("userId"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return Optional.of(u);
      } else {
        return Optional.empty();
      }
    });
  }

  @Override
  public boolean existsByEmail(String username) {
    return false;
  }
}
