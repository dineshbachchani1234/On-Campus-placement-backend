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
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    // prepare default values
    int    appId   = interview.getApplication().getApplicationId();
    int    recId   = interview.getRecruiter().getRecruiterId();
    Timestamp dt   = Timestamp.valueOf(interview.getInterviewDate());
    String status  = interview.getStatus() != null
        ? interview.getStatus().toString()
        : Interview.InterviewStatus.SCHEDULED.toString();
    String feedback= interview.getFeedback();
    String result  = interview.getResult() != null
        ? interview.getResult().toString()
        : Interview.InterviewResult.PENDING.toString();

    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_interview")
        .declareParameters(
            new SqlParameter   ("p_application_id", Types.INTEGER),
            new SqlParameter   ("p_recruiter_id",   Types.INTEGER),
            new SqlParameter   ("p_interview_dt",   Types.TIMESTAMP),
            new SqlParameter   ("p_status",         Types.VARCHAR),
            new SqlParameter   ("p_feedback",       Types.LONGVARCHAR),
            new SqlParameter("p_result",         Types.VARCHAR),
            new SqlOutParameter("p_new_id",         Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_application_id", appId)
        .addValue("p_recruiter_id",   recId)
        .addValue("p_interview_dt",   dt)
        .addValue("p_status",         status)
        .addValue("p_feedback",       feedback)
        .addValue("p_result",         result);

    Map<String,Object> out = call.execute(in);
    Integer newId = (Integer) out.get("p_new_id");
    if (newId != null) {
      interview.setInterviewId(newId);
    } else {
      System.err.println("Warning: failed to retrieve new interview ID");
    }

    return interview;
  }


  @Override
  public Interview update(Interview interview) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_interview")
        .declareParameters(
            new SqlParameter   ("p_interview_id",   Types.INTEGER),
            new SqlParameter   ("p_application_id", Types.INTEGER),
            new SqlParameter   ("p_recruiter_id",   Types.INTEGER),
            new SqlParameter   ("p_interview_dt",   Types.TIMESTAMP),
            new SqlParameter   ("p_status",         Types.VARCHAR),
            new SqlParameter   ("p_feedback",       Types.LONGVARCHAR),
            new SqlParameter   ("p_result",         Types.VARCHAR),
            new SqlOutParameter("p_rows_updated",   Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_interview_id",   interview.getInterviewId())
        .addValue("p_application_id", interview.getApplication().getApplicationId())
        .addValue("p_recruiter_id",   interview.getRecruiter().getRecruiterId())
        .addValue("p_interview_dt",   Timestamp.valueOf(interview.getInterviewDate()))
        .addValue("p_status",         interview.getStatus().toString())
        .addValue("p_feedback",       interview.getFeedback())
        .addValue("p_result",         interview.getResult().toString());

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no interview row updated for ID="
          + interview.getInterviewId());
    }

    return interview;
  }


  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_interview_by_id")
          .declareParameters(
              new SqlParameter   ("p_interview_id", Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_interview_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_delete_interview_by_id failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public Optional<Interview> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interview_details_by_id")
          .returningResultSet("rs", (rs, rowNum) -> {
            Interview i = new Interview();
            i.setInterviewId(rs.getInt("interviewID"));

            // Build Application
            Application application = new Application();
            application.setApplicationId(rs.getInt("app_id"));
            String appStatus = rs.getString("app_status");
            if (appStatus != null) {
              application.setStatus(Application.ApplicationStatus.valueOf(appStatus));
            }

            // Build JobListing
            JobListing job = new JobListing();
            job.setJobId(rs.getInt("job_id"));
            job.setTitle(rs.getString("job_title"));
            job.setDescription(rs.getString("job_description"));
            job.setSalary(rs.getBigDecimal("job_salary"));
            String jt = rs.getString("job_type");
            if (jt != null) {
              job.setJobType(JobListing.JobType.valueOf(jt));
            }
            job.setDeadline(rs.getDate("job_deadline").toLocalDate());
            job.setPostDate(rs.getDate("job_postDate").toLocalDate());
            job.setActive(rs.getBoolean("job_isActive"));

            // Build Company
            Company company = new Company();
            company.setCompanyId(rs.getInt("company_id"));
            company.setCompanyName(rs.getString("company_name"));
            company.setIndustry(rs.getString("company_industry"));
            job.setCompany(company);

            application.setJob(job);
            i.setApplication(application);

            // Load recruiter separately
            int recId = rs.getInt("recruiterID");
            recruiterRepository.findById(recId).ifPresent(i::setRecruiter);

            // Interview properties
            i.setInterviewDate(rs.getTimestamp("interviewDate").toLocalDateTime());
            String ivStat = rs.getString("iv_status");
            if (ivStat != null) {
              i.setStatus(Interview.InterviewStatus.valueOf(ivStat));
            }
            String ivRes = rs.getString("iv_result");
            if (ivRes != null) {
              i.setResult(Interview.InterviewResult.valueOf(ivRes));
            }
            i.setFeedback(rs.getString("feedback"));

            return i;
          });

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_interview_id", id);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Interview> list = (List<Interview>) out.get("rs");

      return list.isEmpty()
          ? Optional.empty()
          : Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("sp_get_interview_details_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public List<Interview> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_interviews")
          .returningResultSet("rs", interviewRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Interview> list = (List<Interview>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_interviews failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Interview> findByApplicationId(Integer applicationId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interviews_by_application_id")
          .returningResultSet("rs", interviewRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_application_id", applicationId);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Interview> list = (List<Interview>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_interviews_by_application_id failed: " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public List<Interview> findByRecruiterId(Integer recruiterId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interviews_by_recruiter_id")
          .returningResultSet("rs", interviewRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_recruiter_id", recruiterId);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Interview> list = (List<Interview>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_interviews_by_recruiter_id failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Interview> findByStatus(Interview.InterviewStatus status) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interviews_by_status")
          .returningResultSet("rs", interviewRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_status", status.toString());

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Interview> list = (List<Interview>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_interviews_by_status failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Interview> findByResult(Interview.InterviewResult result) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interviews_by_result")
          .returningResultSet("rs", interviewRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_result", result.toString());

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Interview> list = (List<Interview>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: use proper logging
      System.err.println("sp_get_interviews_by_result failed: " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public List<Interview> findUpcomingInterviews(LocalDateTime date) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_upcoming_interviews")
          .returningResultSet("rs", interviewRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_from_datetime", Timestamp.valueOf(date));

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Interview> list = (List<Interview>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_upcoming_interviews failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public boolean updateStatus(Integer interviewId, Interview.InterviewStatus status) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_update_interview_status")
          .declareParameters(
              new SqlParameter   ("p_interview_id", Types.INTEGER),
              new SqlParameter   ("p_status",       Types.VARCHAR),
              new SqlOutParameter("p_rows",         Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_interview_id", interviewId)
          .addValue("p_status",       status.toString());

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_update_interview_status failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public boolean updateResult(Integer interviewId, Interview.InterviewResult result, String feedback) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_update_interview_result")
          .declareParameters(
              new SqlParameter   ("p_interview_id", Types.INTEGER),
              new SqlParameter   ("p_result",       Types.VARCHAR),
              new SqlParameter   ("p_feedback",     Types.LONGVARCHAR),
              new SqlOutParameter("p_rows",         Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_interview_id", interviewId)
          .addValue("p_result",       result.toString())
          .addValue("p_feedback",     feedback);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_update_interview_result failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public List<Interview> findByStudentId(Integer studentId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interviews_by_student_id")
          .returningResultSet("rs", (rs, rowNum) -> {
            Interview i = new Interview();
            i.setInterviewId(rs.getInt("interviewID"));

            // Build Application
            Application application = new Application();
            application.setApplicationId(rs.getInt("app_id"));
            String appStatus = rs.getString("app_status");
            if (appStatus != null) {
              application.setStatus(Application.ApplicationStatus.valueOf(appStatus));
            }

            // Build JobListing
            JobListing job = new JobListing();
            job.setJobId(rs.getInt("job_id"));
            job.setTitle(rs.getString("job_title"));
            job.setDescription(rs.getString("job_description"));
            job.setSalary(rs.getBigDecimal("job_salary"));
            String jt = rs.getString("job_type");
            if (jt != null) {
              job.setJobType(JobListing.JobType.valueOf(jt));
            }
            job.setDeadline(rs.getDate("job_deadline").toLocalDate());
            job.setPostDate(rs.getDate("job_postDate").toLocalDate());
            job.setActive(rs.getBoolean("job_isActive"));

            // Build Company
            Company company = new Company();
            company.setCompanyId(rs.getInt("company_id"));
            company.setCompanyName(rs.getString("company_name"));
            company.setIndustry(rs.getString("company_industry"));
            job.setCompany(company);

            application.setJob(job);
            i.setApplication(application);

            // Load recruiter (via your existing repo)
            int recId = rs.getInt("recruiterID");
            recruiterRepository.findById(recId).ifPresent(i::setRecruiter);

            // Interview properties
            i.setInterviewDate(rs.getTimestamp("interviewDate").toLocalDateTime());
            String ivStat = rs.getString("iv_status");
            if (ivStat != null) {
              i.setStatus(Interview.InterviewStatus.valueOf(ivStat));
            }
            String ivRes = rs.getString("iv_result");
            if (ivRes != null) {
              i.setResult(Interview.InterviewResult.valueOf(ivRes));
            }
            i.setFeedback(rs.getString("feedback"));

            return i;
          });

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_student_id", studentId);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Interview> interviews = (List<Interview>) out.get("rs");
      return interviews;

    } catch (Exception e) {
      System.err.println("sp_get_interviews_by_student_id failed: " + e.getMessage());
      return List.of();
    }
  }

}