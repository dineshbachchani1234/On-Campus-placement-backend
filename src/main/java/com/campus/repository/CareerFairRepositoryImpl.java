package com.campus.repository;

import com.campus.model.CareerFair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CareerFairRepositoryImpl implements CareerFairRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(CareerFair fair) {
    String sql = "INSERT INTO careerfair (title, description, date, location) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, fair.getTitle(), fair.getDescription(), fair.getDate(), fair.getLocation());
  }

  @Override
  public CareerFair findById(int id) {
    String sql = "SELECT * FROM careerfair WHERE fairID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new CareerFairRowMapper());
  }

  @Override
  public List<CareerFair> findAll() {
    String sql = "SELECT * FROM careerfair";
    return jdbcTemplate.query(sql, new CareerFairRowMapper());
  }

  @Override
  public void update(CareerFair fair) {
    String sql = "UPDATE careerfair SET title = ?, description = ?, date = ?, location = ? WHERE fairID = ?";
    jdbcTemplate.update(sql, fair.getTitle(), fair.getDescription(), fair.getDate(), fair.getLocation(), fair.getFairId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM careerfair WHERE fairID = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class CareerFairRowMapper implements RowMapper<CareerFair> {
    @Override
    public CareerFair mapRow(ResultSet rs, int rowNum) throws SQLException {
      CareerFair fair = new CareerFair();
      fair.setFairId(rs.getInt("fairID"));
      fair.setTitle(rs.getString("title"));
      fair.setDescription(rs.getString("description"));
      fair.setDate(rs.getString("date"));
      fair.setLocation(rs.getString("location"));
      return fair;
    }
  }
}
