package com.campus.repository;

import com.campus.model.Application;
import com.campus.model.Company;
import com.campus.model.Interview;
import com.campus.model.JobListing;
import com.campus.model.Recruiter;

import java.util.ArrayList;
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
    String sql = "SELECT i.*, a.applicationID AS app_id, a.status AS app_status,\n"
        + "                 j.jobID AS job_id, j.title AS job_title, j.description AS job_description,\n"
        + "                 j.salary AS job_salary, j.jobType AS job_type, j.deadline AS job_deadline,\n"
        + "                 j.postDate AS job_postDate, j.isActive AS job_isActive,\n"
        + "                 c.companyID AS company_id, c.companyname AS company_name, c.industry AS company_industry\n"
        + "                 FROM interview i\n"
        + "                 JOIN application a ON i.applicationID = a.applicationID\n"
        + "                 JOIN joblisting j ON a.jobID = j.jobID\n"
        + "                 JOIN company c ON j.companyID = c.companyID\n"
        + "                 WHERE i.interviewID = ?";

    try {
      Interview interview = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
        Interview i = new Interview();
        i.setInterviewId(rs.getInt("interviewID"));

        // Build the application
        Application application = new Application();
        application.setApplicationId(rs.getInt("app_id"));

        String appStatusStr = rs.getString("app_status");
        if (appStatusStr != null) {
          application.setStatus(Application.ApplicationStatus.valueOf(appStatusStr));
        }

        // Build the job
        JobListing job = new JobListing();
        job.setJobId(rs.getInt("job_id"));
        job.setTitle(rs.getString("job_title"));
        job.setDescription(rs.getString("job_description"));
        job.setSalary(rs.getBigDecimal("job_salary"));

        String jobTypeStr = rs.getString("job_type");
        if (jobTypeStr != null) {
          job.setJobType(JobListing.JobType.valueOf(jobTypeStr));
        }

        job.setDeadline(rs.getDate("job_deadline").toLocalDate());
        job.setPostDate(rs.getDate("job_postDate").toLocalDate());
        job.setActive(rs.getBoolean("job_isActive"));

        // Build the company
        Company company = new Company();
        company.setCompanyId(rs.getInt("company_id"));
        company.setCompanyName(rs.getString("company_name"));
        company.setIndustry(rs.getString("company_industry"));

        // Set the relationships
        job.setCompany(company);
        application.setJob(job);
        i.setApplication(application);

        // Get recruiter (still need to load it separately)
        Integer recruiterId = rs.getInt("recruiterID");
        Optional<Recruiter> recruiter = recruiterRepository.findById(recruiterId);
        recruiter.ifPresent(i::setRecruiter);

        // Set interview properties
        i.setInterviewDate(rs.getTimestamp("interviewDate").toLocalDateTime());

        String statusStr = rs.getString("status");
        if (statusStr != null) {
          i.setStatus(Interview.InterviewStatus.valueOf(statusStr));
        }

        String resultStr = rs.getString("result");
        if (resultStr != null) {
          i.setResult(Interview.InterviewResult.valueOf(resultStr));
        }

        i.setFeedback(rs.getString("feedback"));

        return i;
      }, id);

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

  @Override
  public List<Interview> findByStudentId(Integer studentId) {
    // First, get all the interview IDs for this student
    String idSql = "SELECT i.interviewID FROM interview i " +
        "JOIN application a ON i.applicationID = a.applicationID " +
        "WHERE a.studentID = ?";

    List<Integer> interviewIds = jdbcTemplate.queryForList(idSql, Integer.class, studentId);

    // Then load each interview with full details
    List<Interview> interviews = new ArrayList<>();
    for (Integer interviewId : interviewIds) {
      findById(interviewId).ifPresent(interviews::add);
    }

    return interviews;
  }
}