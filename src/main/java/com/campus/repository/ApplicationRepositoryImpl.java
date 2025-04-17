package com.campus.repository;

import com.campus.model.Application;
import com.campus.model.JobListing;
import com.campus.model.Student;

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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    // prepare IN values (using defaults if null)
    int studentId = application.getStudent().getStudentId();
    int jobId     = application.getJob().getJobId();
    java.sql.Date appDate = application.getApplicationDate() != null
        ? java.sql.Date.valueOf(application.getApplicationDate())
        : java.sql.Date.valueOf(LocalDate.now());
    String status = application.getStatus() != null
        ? application.getStatus().toString()
        : Application.ApplicationStatus.PENDING.toString();

    // SimpleJdbcCall for the proc
    SimpleJdbcCall insertProc = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_application")
        .declareParameters(
            new SqlParameter("p_student_id",   Types.INTEGER),
            new SqlParameter   ("p_job_id",       Types.INTEGER),
            new SqlParameter   ("p_app_date",     Types.DATE),
            new SqlParameter   ("p_status",       Types.VARCHAR),
            new SqlOutParameter("p_new_id",       Types.INTEGER)
        );

    // execute
    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_student_id", studentId)
        .addValue("p_job_id",     jobId)
        .addValue("p_app_date",   appDate)
        .addValue("p_status",     status);

    Map<String, Object> out = insertProc.execute(in);
    Integer newId = (Integer) out.get("p_new_id");
    application.setApplicationId(newId);

    return application;
  }


  @Override
  public Application update(Application application) {
    // Prepare the proc call
    SimpleJdbcCall updateProc = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_application")
        .declareParameters(
            new SqlParameter  ("p_application_id", Types.INTEGER),
            new SqlParameter  ("p_student_id",     Types.INTEGER),
            new SqlParameter  ("p_job_id",         Types.INTEGER),
            new SqlParameter  ("p_app_date",       Types.DATE),
            new SqlParameter  ("p_status",         Types.VARCHAR),
            new SqlOutParameter("p_rows_affected", Types.INTEGER)
        );

    // Map IN parameters
    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_application_id", application.getApplicationId())
        .addValue("p_student_id",     application.getStudent().getStudentId())
        .addValue("p_job_id",         application.getJob().getJobId())
        .addValue("p_app_date",       Date.valueOf(application.getApplicationDate()))
        .addValue("p_status",         application.getStatus().toString());

    // Execute
    Map<String, Object> out = updateProc.execute(in);
    Integer rows = (Integer) out.get("p_rows_affected");

    if (rows == null || rows == 0) {
      // you can throw an exception here if you like
      System.err.println("No rows updated for applicationID="
          + application.getApplicationId());
    }

    return application;
  }


  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall deleteProc = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_application_by_id")
          .declareParameters(
              new SqlParameter   ("p_app_id",       Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_app_id", id);

      Map<String,Object> out = deleteProc.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("Failed to delete application: " + e.getMessage());
      return false;
    }
  }


  @Override
  public Optional<Application> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_application_by_id")
          .returningResultSet("rs", (rs, rowNum) -> {
            Application app = new Application();
            app.setApplicationId(rs.getInt("applicationID"));

            Student student = new Student();
            student.setStudentId(rs.getInt("studentID"));
            app.setStudent(student);

            JobListing job = new JobListing();
            job.setJobId(rs.getInt("jobID"));
            app.setJob(job);

            app.setApplicationDate(rs.getDate("applicationDate").toLocalDate());
            app.setStatus(Application.ApplicationStatus.valueOf(rs.getString("status")));

            // load full student and job
            studentRepository.findById(student.getStudentId()).ifPresent(app::setStudent);
            jobListingRepository.findById(job.getJobId()).ifPresent(app::setJob);

            return app;
          });

      Map<String, Object> out = call.execute(Map.of("p_app_id", id));

      @SuppressWarnings("unchecked")
      List<Application> list = (List<Application>) out.get("rs");
      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("sp_get_application_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public List<Application> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_applications")
          .returningResultSet("rs", applicationRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Application> list = (List<Application>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: use real logging
      System.err.println("sp_get_all_applications failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Application> findByStudentId(Integer studentId) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_get_applications_by_student_id")
        .returningResultSet("rs", applicationRowMapper);

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_student_id", studentId);

    Map<String, Object> out = call.execute(in);
    @SuppressWarnings("unchecked")
    List<Application> apps = (List<Application>) out.get("rs");
    return apps;
  }

  @Override
  public Optional<Application> findByJobIdAndStudentId(Integer jobId, Integer studentId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_application_by_job_and_student") // Assumed SP name
          .returningResultSet("rs", applicationRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_job_id", jobId)
          .addValue("p_student_id", studentId);

      Map<String, Object> out = call.execute(in);

      @SuppressWarnings("unchecked")
      List<Application> list = (List<Application>) out.get("rs");
      if (list.isEmpty()) {
        return Optional.empty();
      }
      // Assuming jobID + studentID is unique for applications
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty(); // No application found
    } catch (Exception e) {
      System.err.println("sp_get_application_by_job_and_student failed: " + e.getMessage());
      return Optional.empty(); // Or rethrow as a runtime exception
    }
  }


  @Override
  public List<Application> findByJobId(Integer jobId) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_get_applications_by_job_id")
        .returningResultSet("rs", applicationRowMapper);

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_job_id", jobId);

    Map<String, Object> out = call.execute(in);
    @SuppressWarnings("unchecked")
    List<Application> apps = (List<Application>) out.get("rs");
    return apps;
  }


  @Override
  public List<Application> findByStatus(Application.ApplicationStatus status) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_get_applications_by_status")
        .returningResultSet("rs", applicationRowMapper);

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_status", status.toString());

    Map<String, Object> out = call.execute(in);
    @SuppressWarnings("unchecked")
    List<Application> apps = (List<Application>) out.get("rs");
    return apps;
  }


  @Override
  public boolean existsByStudentIdAndJobId(Integer studentId, Integer jobId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_exists_application_by_student_and_job")
          .declareParameters(
              new SqlParameter   ("p_student_id", Types.INTEGER),
              new SqlParameter   ("p_job_id",     Types.INTEGER),
              new SqlOutParameter("p_count",      Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_student_id", studentId)
          .addValue("p_job_id",     jobId);

      Map<String, Object> out = call.execute(in);
      Integer count = (Integer) out.get("p_count");
      return count != null && count > 0;

    } catch (Exception e) {
      System.err.println("sp_exists_application_by_student_and_job failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public boolean updateStatus(Integer applicationId, Application.ApplicationStatus status) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_update_application_status")
          .declareParameters(
              new SqlParameter   ("p_app_id", Types.INTEGER),
              new SqlParameter   ("p_status", Types.VARCHAR),
              new SqlOutParameter("p_rows",   Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_app_id", applicationId)
          .addValue("p_status", status.toString());

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_update_application_status failed: " + e.getMessage());
      return false;
    }
  }

}
