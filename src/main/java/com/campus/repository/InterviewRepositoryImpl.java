package com.campus.repository;

import com.campus.model.Application;
import com.campus.model.Interview;
import com.campus.model.Recruiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InterviewRepositoryImpl implements InterviewRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private ApplicationRepository applicationRepository;

  @Autowired
  private RecruiterRepository recruiterRepository;

  private RowMapper<Interview> interviewRowMapper = (rs, rowNum) -> {
    Interview interview = new Interview();
    interview.setInterviewId(rs.getInt("interviewID"));

    Application application = new Application();
    application.setApplicationId(rs.getInt("applicationID"));
    interview.setApplication(application);

    Recruiter recruiter = new Recruiter();
    recruiter.setRecruiterId(rs.getInt("recruiterID"));
    interview.setRecruiter(recruiter);

    interview.setInterviewDate(rs.getTimestamp("interviewDate").toLocalDateTime());

    String statusStr = rs.getString("status");
    if (statusStr != null) {
      interview.setStatus(Interview.InterviewStatus.valueOf(statusStr));
    }

    interview.setFeedback(rs.getString("feedback"));

    String resultStr = rs.getString("result");
    if (resultStr != null) {
      interview.setResult(Interview.InterviewResult.valueOf(resultStr));
    }

    // Load the application and recruiter
    applicationRepository.findById(application.getApplicationId()).ifPresent(interview::setApplication);
    recruiterRepository.findById(recruiter.getRecruiterId()).ifPresent(interview::setRecruiter);

    return interview;
  };

  @Override
  public Interview save(Interview interview) {
    String sql = "INSERT INTO interview (applicationID, recruiterID, interviewDate, status, feedback, result) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, interview.getApplication().getApplicationId());
      ps.setInt(2, interview.getRecruiter().getRecruiterId());
      ps.setTimestamp(3, Timestamp.valueOf(interview.getInterviewDate()));
      ps.setString(4, interview.getStatus() != null ?
          interview.getStatus().toString() :
          Interview.InterviewStatus.SCHEDULED.toString());
      ps.setString(5, interview.getFeedback());
      ps.setString(6, interview.getResult() != null ?
          interview.getResult().toString() :
          Interview.InterviewResult.PENDING.toString());
      return ps;
    }, keyHolder);

    interview.setInterviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    return interview;
  }

  @Override
  public Interview update(Interview interview) {
    String sql = "UPDATE interview SET applicationID = ?, recruiterID = ?, interviewDate = ?, " +
        "status = ?, feedback = ?, result = ? WHERE interviewID = ?";

    jdbcTemplate.update(sql,
        interview.getApplication().getApplicationId(),
        interview.getRecruiter().getRecruiterId(),
        Timestamp.valueOf(interview.getInterviewDate()),
        interview.getStatus().toString(),
        interview.getFeedback(),
        interview.getResult().toString(),
        interview.getInterviewId()
    );

    return interview;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM interview WHERE interviewID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<Interview> findById(Integer id) {
    String sql = "SELECT * FROM interview WHERE interviewID = ?";

    try {
      Interview interview = jdbcTemplate.queryForObject(sql, interviewRowMapper, id);
      return Optional.ofNullable(interview);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Interview> findAll() {
    String sql = "SELECT * FROM interview";
    return jdbcTemplate.query(sql, interviewRowMapper);
  }

  @Override
  public List<Interview> findByApplicationId(Integer applicationId) {
    String sql = "SELECT * FROM interview WHERE applicationID = ?";
    return jdbcTemplate.query(sql, interviewRowMapper, applicationId);
  }

  @Override
  public List<Interview> findByRecruiterId(Integer recruiterId) {
    String sql = "SELECT * FROM interview WHERE recruiterID = ?";
    return jdbcTemplate.query(sql, interviewRowMapper, recruiterId);
  }

  @Override
  public List<Interview> findByStatus(Interview.InterviewStatus status) {
    String sql = "SELECT * FROM interview WHERE status = ?";
    return jdbcTemplate.query(sql, interviewRowMapper, status.toString());
  }

  @Override
  public List<Interview> findByResult(Interview.InterviewResult result) {
    String sql = "SELECT * FROM interview WHERE result = ?";
    return jdbcTemplate.query(sql, interviewRowMapper, result.toString());
  }

  @Override
  public List<Interview> findUpcomingInterviews(LocalDateTime date) {
    String sql = "SELECT * FROM interview WHERE interviewDate > ?";
    return jdbcTemplate.query(sql, interviewRowMapper, Timestamp.valueOf(date));
  }

  @Override
  public boolean updateStatus(Integer interviewId, Interview.InterviewStatus status) {
    String sql = "UPDATE interview SET status = ? WHERE interviewID = ?";
    int rowsAffected = jdbcTemplate.update(sql, status.toString(), interviewId);
    return rowsAffected > 0;
  }

  @Override
  public boolean updateResult(Integer interviewId, Interview.InterviewResult result, String feedback) {
    String sql = "UPDATE interview SET result = ?, feedback = ? WHERE interviewID = ?";
    int rowsAffected = jdbcTemplate.update(sql, result.toString(), feedback, interviewId);
    return rowsAffected > 0;
  }
}