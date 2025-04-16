package com.campus.repository;

import com.campus.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Repository
public class UserRepositoryImpl implements UserRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private RowMapper<User> userRowMapper = (rs, rowNum) -> {
    User user = new User();
    user.setUserId(rs.getInt("userID"));
    user.setFirstName(rs.getString("firstName"));
    user.setLastName(rs.getString("lastName"));
    user.setEmail(rs.getString("email"));
    user.setPassword(rs.getString("password"));
    user.setRole(User.Role.valueOf(rs.getString("role")));
    return user;
  };

  @Override
  public User save(User user) {
    String sql = "INSERT INTO user (firstName, lastName, email, password, role) VALUES (?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setString(3, user.getEmail());
      ps.setString(4, user.getPassword());
      ps.setString(5, user.getRole().toString());
      return ps;
    }, keyHolder);

    user.setUserId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    return user;
  }

  @Override
  public User update(User user) {
    String sql = "UPDATE user SET firstName = ?, lastName = ?, email = ?, password = ?, role = ? WHERE userID = ?";

    jdbcTemplate.update(sql,
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getPassword(),
        user.getRole().toString(),
        user.getUserId()
    );

    return user;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM user WHERE userID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<User> findById(Integer id) {
    String sql = "SELECT * FROM user WHERE userID = ?";

    try {
      User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
      return Optional.ofNullable(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<User> findAll() {
    String sql = "SELECT * FROM user";
    return jdbcTemplate.query(sql, userRowMapper);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM user WHERE email = ?";

    try {
      User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
      return Optional.ofNullable(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean existsByEmail(String email) {
    String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
    try {
      Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
      return count != null && count > 0;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
