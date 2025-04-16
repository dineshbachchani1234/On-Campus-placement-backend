package com.campus.repository;

import com.campus.model.Application;
import com.campus.model.JobListing;
import com.campus.model.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private JobListingRepository jobListingRepository;

  private RowMapper<Application> applicationRowMapper = (rs, rowNum) -> {
    Application application = new Application();
    application.setApplicationId(rs.getInt("applicationID"));

    Student student = new Student();
    student.setStudentId(rs.getInt("studentID"));
    application.setStudent(student);

    JobListing job = new JobListing();
    job.setJobId(rs.getInt("jobID"));
    application.setJob(job);

    application.setApplicationDate(rs.getDate("applicationDate").toLocalDate());

    String statusStr = rs.getString("status");
    if (statusStr != null) {
      application.setStatus(Application.ApplicationStatus.valueOf(statusStr));
    }

    // Load the student and job
    studentRepository.findById(student.getStudentId()).ifPresent(application::setStudent);
    jobListingRepository.findById(job.getJobId()).ifPresent(application::setJob);

    return application;
  };

  @Override
  public Application save(Application application) {
    String sql = "INSERT INTO application (studentID, jobID, applicationDate, status) VALUES (?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, application.getStudent().getStudentId());
      ps.setInt(2, application.getJob().getJobId());
      ps.setDate(3, application.getApplicationDate() != null ?
          Date.valueOf(application.getApplicationDate()) :
          Date.valueOf(LocalDate.now()));
      ps.setString(4, application.getStatus() != null ?
          application.getStatus().toString() :
          Application.ApplicationStatus.PENDING.toString());
      return ps;
    }, keyHolder);

    application.setApplicationId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    return application;
  }

  @Override
  public Application update(Application application) {
    String sql = "UPDATE application SET studentID = ?, jobID = ?, applicationDate = ?, status = ? " +
        "WHERE applicationID = ?";

    jdbcTemplate.update(sql,
        application.getStudent().getStudentId(),
        application.getJob().getJobId(),
        Date.valueOf(application.getApplicationDate()),
        application.getStatus().toString(),
        application.getApplicationId()
    );

    return application;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM application WHERE applicationID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<Application> findById(Integer id) {
    String sql = "SELECT * FROM application WHERE applicationID = ?";

    try {
      Application application = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
        Application app = new Application();
        app.setApplicationId(rs.getInt("applicationID"));

        Student student = new Student();
        student.setStudentId(rs.getInt("studentID"));
        app.setStudent(student);

        JobListing job = new JobListing();
        job.setJobId(rs.getInt("jobID"));
        app.setJob(job);

        app.setApplicationDate(rs.getDate("applicationDate").toLocalDate());

        String statusStr = rs.getString("status");
        if (statusStr != null) {
          app.setStatus(Application.ApplicationStatus.valueOf(statusStr));
        }

        // Load the student and job with full details
        studentRepository.findById(student.getStudentId()).ifPresent(app::setStudent);

        // Make sure job is fully loaded with company
        Optional<JobListing> fullJob = jobListingRepository.findById(job.getJobId());
        if (fullJob.isPresent()) {
          app.setJob(fullJob.get());
        }

        return app;
      }, id);

      return Optional.ofNullable(application);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Application> findAll() {
    String sql = "SELECT * FROM application";
    return jdbcTemplate.query(sql, applicationRowMapper);
  }

  @Override
  public List<Application> findByStudentId(Integer studentId) {
    String sql = "SELECT a.*, j.jobID AS job_id, j.title AS job_title, j.description AS job_description,\n"
        + "               j.salary AS job_salary, j.jobType AS job_type, j.deadline AS job_deadline,\n"
        + "               j.postDate AS job_postDate, j.isActive AS job_isActive,\n"
        + "               c.companyID AS company_id, c.companyname AS company_name, c.industry AS company_industry\n"
        + "               FROM application a\n"
        + "               JOIN joblisting j ON a.jobID = j.jobID\n"
        + "               JOIN company c ON j.companyID = c.companyID\n"
        + "               WHERE a.studentID = ?";
    return jdbcTemplate.query(sql, applicationRowMapper, studentId);
  }

  @Override
  public List<Application> findByJobId(Integer jobId) {
    String sql = "SELECT * FROM application WHERE jobID = ?";
    return jdbcTemplate.query(sql, applicationRowMapper, jobId);
  }

  @Override
  public List<Application> getApplicationsByJobId(Integer jobId) {
    String sql = "SELECT * FROM application WHERE jobID = ?";
    return jdbcTemplate.query(sql, applicationRowMapper, jobId);
  }

  @Override
  public List<Application> findByStatus(Application.ApplicationStatus status) {
    String sql = "SELECT * FROM application WHERE status = ?";
    return jdbcTemplate.query(sql, applicationRowMapper, status.toString());
  }

  @Override
  public boolean existsByStudentIdAndJobId(Integer studentId, Integer jobId) {
    String sql = "SELECT COUNT(*) FROM application WHERE studentID = ? AND jobID = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, jobId);
    return count != null && count > 0;
  }

  @Override
  public boolean updateStatus(Integer applicationId, Application.ApplicationStatus status) {
    String sql = "UPDATE application SET status = ? WHERE applicationID = ?";
    int rowsAffected = jdbcTemplate.update(sql, status.toString(), applicationId);
    return rowsAffected > 0;
  }
}