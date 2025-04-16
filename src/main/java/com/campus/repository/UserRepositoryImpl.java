package com.campus.repository;

import com.campus.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;
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
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_user_by_id")
          .declareParameters(
              new SqlParameter("p_user_id", Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_user_id", id);

      Map<String,Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      // TODO: use proper logging
      System.err.println("Failed to delete user: " + e.getMessage());
      return false;
    }
  }

  @Override
  public Optional<User> findById(Integer id) {
    try {
      // Configure the call once (could be a field)
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_user_by_id")
          .returningResultSet("rs", userRowMapper);

      // Pass IN param
      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_user_id", id);

      // Execute and pull out the result set
      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<User> users = (List<User>) out.get("rs");

      return users.isEmpty()
          ? Optional.empty()
          : Optional.of(users.get(0));

    } catch (Exception e) {
      // handle/log
      System.err.println("Procedure sp_get_user_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public List<User> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_users")
          .returningResultSet("rs", userRowMapper);

      // no IN params
      Map<String,Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<User> users = (List<User>) out.get("rs");
      return users;

    } catch (Exception e) {
      // TODO: proper logging
      System.err.println("Procedure sp_get_all_users failed: " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_user_by_email")
          .returningResultSet("rs", userRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_email", email);

      Map<String,Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<User> users = (List<User>) out.get("rs");

      if (users.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(users.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("Procedure sp_get_user_by_email failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public boolean existsByEmail(String email) {
    try {
      SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_count_user_by_email")
          .declareParameters(
              new SqlParameter("p_email", Types.VARCHAR),
              new SqlOutParameter("p_count", Types.INTEGER)
          );
      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_email", email);

      Map<String, Object> out = proc.execute(in);
      Integer count = (Integer) out.get("p_count");
      return count != null && count > 0;

    } catch (Exception e) {
      System.err.println("Procedure call failed: " + e.getMessage());
      return false;
    }
  }

}
