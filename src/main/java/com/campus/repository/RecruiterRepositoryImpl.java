package com.campus.repository;

import com.campus.model.Recruiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RecruiterRepositoryImpl implements RecruiterRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Recruiter recruiter) {
    String sql = "INSERT INTO recruiter (firstName, lastName, email, position) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, recruiter.getFirstName(), recruiter.getLastName(), recruiter.getEmail(), recruiter.getPosition());
  }

  @Override
  public Recruiter findById(int id) {
    String sql = "SELECT * FROM recruiter WHERE recruiterID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new RecruiterRowMapper());
  }

  @Override
  public List<Recruiter> findAll() {
    String sql = "SELECT * FROM recruiter";
    return jdbcTemplate.query(sql, new RecruiterRowMapper());
  }

  @Override
  public void update(Recruiter recruiter) {
    String sql = "UPDATE recruiter SET firstName = ?, lastName = ?, email = ?, position = ? WHERE recruiterID = ?";
    jdbcTemplate.update(sql, recruiter.getFirstName(), recruiter.getLastName(), recruiter.getEmail(), recruiter.getPosition(), recruiter.getRecruiterId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM recruiter WHERE recruiterID = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class RecruiterRowMapper implements RowMapper<Recruiter> {
    @Override
    public Recruiter mapRow(ResultSet rs, int rowNum) throws SQLException {
      Recruiter recruiter = new Recruiter();
      recruiter.setRecruiterId(rs.getInt("recruiterID"));
      recruiter.setFirstName(rs.getString("firstName"));
      recruiter.setLastName(rs.getString("lastName"));
      recruiter.setEmail(rs.getString("email"));
      recruiter.setPosition(rs.getString("position"));
      return recruiter;
    }
  }
}
