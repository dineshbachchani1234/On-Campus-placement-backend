package com.campus.repository;

import com.campus.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JobRepositoryImpl implements JobRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Job job) {
    String sql = "INSERT INTO joblisting (title, description, salary, job_type) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, job.getTitle(), job.getDescription(), job.getSalary(), job.getJobType());
  }

  @Override
  public Job findById(int id) {
    String sql = "SELECT * FROM joblisting WHERE jobID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new JobRowMapper());
  }

  @Override
  public List<Job> findAll() {
    String sql = "SELECT * FROM joblisting";
    return jdbcTemplate.query(sql, new JobRowMapper());
  }

  @Override
  public void update(Job job) {
    String sql = "UPDATE joblisting SET title = ?, description = ?, salary = ?, job_type = ? WHERE jobID = ?";
    jdbcTemplate.update(sql, job.getTitle(), job.getDescription(), job.getSalary(), job.getJobType(), job.getJobId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM joblisting WHERE jobID = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class JobRowMapper implements RowMapper<Job> {
    @Override
    public Job mapRow(ResultSet rs, int rowNum) throws SQLException {
      Job job = new Job();
      job.setJobId(rs.getInt("jobID"));
      job.setTitle(rs.getString("title"));
      job.setDescription(rs.getString("description"));
      job.setSalary(rs.getDouble("salary"));
      job.setJobType(rs.getString("job_type"));
      return job;
    }
  }
}