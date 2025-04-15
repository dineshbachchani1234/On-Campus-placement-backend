package com.campus.repository;

import com.campus.model.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Application app) {
    String sql = "INSERT INTO application (studentID, jobID, applicationDate, status) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, app.getStudentId(), app.getJobId(), app.getApplicationDate(), app.getStatus());
  }

  @Override
  public Application findById(int id) {
    String sql = "SELECT * FROM application WHERE applicationID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ApplicationRowMapper());
  }

  @Override
  public List<Application> findAll() {
    String sql = "SELECT * FROM application";
    return jdbcTemplate.query(sql, new ApplicationRowMapper());
  }

  @Override
  public void update(Application app) {
    String sql = "UPDATE application SET studentID = ?, jobID = ?, applicationDate = ?, status = ? WHERE applicationID = ?";
    jdbcTemplate.update(sql, app.getStudentId(), app.getJobId(), app.getApplicationDate(), app.getStatus(), app.getApplicationId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM application WHERE applicationID = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class ApplicationRowMapper implements RowMapper<Application> {
    @Override
    public Application mapRow(ResultSet rs, int rowNum) throws SQLException {
      Application app = new Application();
      app.setApplicationId(rs.getInt("applicationID"));
      app.setStudentId(rs.getInt("studentID"));
      app.setJobId(rs.getInt("jobID"));
      app.setApplicationDate(rs.getString("applicationDate"));
      app.setStatus(rs.getString("status"));
      return app;
    }
  }
}
