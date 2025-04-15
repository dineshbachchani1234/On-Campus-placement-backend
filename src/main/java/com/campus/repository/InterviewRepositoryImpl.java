package com.campus.repository;

import com.campus.model.Interview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InterviewRepositoryImpl implements InterviewRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Interview interview) {
    String sql = "INSERT INTO interview (applicationID, interviewDate, feedback, result) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql, interview.getApplicationId(), interview.getInterviewDate(), interview.getFeedback(), interview.getResult());
  }

  @Override
  public Interview findById(int id) {
    String sql = "SELECT * FROM interview WHERE interviewID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new InterviewRowMapper());
  }

  @Override
  public List<Interview> findAll() {
    String sql = "SELECT * FROM interview";
    return jdbcTemplate.query(sql, new InterviewRowMapper());
  }

  @Override
  public void update(Interview interview) {
    String sql = "UPDATE interview SET applicationID = ?, interviewDate = ?, feedback = ?, result = ? WHERE interviewID = ?";
    jdbcTemplate.update(sql, interview.getApplicationId(), interview.getInterviewDate(), interview.getFeedback(), interview.getResult(), interview.getInterviewId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM interview WHERE interviewID = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class InterviewRowMapper implements RowMapper<Interview> {
    @Override
    public Interview mapRow(ResultSet rs, int rowNum) throws SQLException {
      Interview interview = new Interview();
      interview.setInterviewId(rs.getInt("interviewID"));
      interview.setApplicationId(rs.getInt("applicationID"));
      interview.setInterviewDate(rs.getString("interviewDate"));
      interview.setFeedback(rs.getString("feedback"));
      interview.setResult(rs.getString("result"));
      return interview;
    }
  }
}
