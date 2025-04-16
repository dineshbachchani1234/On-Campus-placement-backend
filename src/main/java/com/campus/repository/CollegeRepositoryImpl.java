package com.campus.repository;

import com.campus.model.College;

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
public class CollegeRepositoryImpl implements CollegeRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private RowMapper<College> collegeRowMapper = (rs, rowNum) -> {
    College college = new College();
    college.setCollegeId(rs.getInt("collegeID"));
    college.setName(rs.getString("name"));
    college.setNumberOfStudents(rs.getInt("numberOfStudents"));
    return college;
  };

  @Override
  public College save(College college) {
    String sql = "INSERT INTO college (name, numberOfStudents) VALUES (?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, college.getName());
      ps.setInt(2, college.getNumberOfStudents() != null ?
          college.getNumberOfStudents() : 0);
      return ps;
    }, keyHolder);

    college.setCollegeId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    return college;
  }

  @Override
  public College update(College college) {
    String sql = "UPDATE college SET name = ?, numberOfStudents = ? WHERE collegeID = ?";

    jdbcTemplate.update(sql,
        college.getName(),
        college.getNumberOfStudents(),
        college.getCollegeId()
    );

    return college;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM college WHERE collegeID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<College> findById(Integer id) {
    String sql = "SELECT * FROM college WHERE collegeID = ?";

    try {
      College college = jdbcTemplate.queryForObject(sql, collegeRowMapper, id);
      return Optional.ofNullable(college);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<College> findAll() {
    String sql = "SELECT * FROM college";
    return jdbcTemplate.query(sql, collegeRowMapper);
  }

  @Override
  public Optional<College> findByName(String name) {
    String sql = "SELECT * FROM college WHERE name = ?";

    try {
      College college = jdbcTemplate.queryForObject(sql, collegeRowMapper, name);
      return Optional.ofNullable(college);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean existsByName(String name) {
    String sql = "SELECT COUNT(*) FROM college WHERE name = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
    return count != null && count > 0;
  }

  @Override
  public boolean updateNumberOfStudents(Integer collegeId, Integer numberOfStudents) {
    String sql = "UPDATE college SET numberOfStudents = ? WHERE collegeID = ?";
    int rowsAffected = jdbcTemplate.update(sql, numberOfStudents, collegeId);
    return rowsAffected > 0;
  }
}