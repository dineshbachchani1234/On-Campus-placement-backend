package com.campus.repository;


import com.campus.model.Application;
import com.campus.model.Company;
import com.campus.model.JobListing;

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

import java.math.BigDecimal;
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
public class JobListingRepositoryImpl implements JobListingRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private CompanyRepository companyRepository;


  private RowMapper<JobListing> jobListingRowMapper = (rs, rowNum) -> {
    JobListing jobListing = new JobListing();
    jobListing.setJobId(rs.getInt("jobID"));

    Company company = new Company();
    company.setCompanyId(rs.getInt("companyID"));
    jobListing.setCompany(company);

    jobListing.setTitle(rs.getString("title"));
    jobListing.setDescription(rs.getString("description"));
    jobListing.setSalary(rs.getBigDecimal("salary"));
    jobListing.setJobType(JobListing.JobType.valueOf(rs.getString("jobType")));
    jobListing.setDeadline(rs.getDate("deadline").toLocalDate());
    jobListing.setPostDate(rs.getDate("postDate").toLocalDate());
    jobListing.setActive(rs.getBoolean("isActive"));

    // Load the company
    companyRepository.findById(company.getCompanyId()).ifPresent(jobListing::setCompany);

    return jobListing;
  };

  @Override
  public JobListing save(JobListing jobListing) {
    // prepare values (use today if postDate is null, default true for isActive)
    int   companyId = jobListing.getCompany().getCompanyId();
    String title    = jobListing.getTitle();
    String desc     = jobListing.getDescription();
    BigDecimal salary = jobListing.getSalary();
    String jobType  = jobListing.getJobType().toString();
    Date  deadline  = Date.valueOf(jobListing.getDeadline());
    Date  postDate  = jobListing.getPostDate() != null
        ? Date.valueOf(jobListing.getPostDate())
        : Date.valueOf(LocalDate.now());
    boolean active  = jobListing.getActive() != null
        ? jobListing.getActive()
        : true;

    SimpleJdbcCall insertProc = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_joblisting_full")
        .declareParameters(
            new SqlParameter   ("p_company_id", Types.INTEGER),
            new SqlParameter   ("p_title",      Types.VARCHAR),
            new SqlParameter   ("p_description",Types.LONGVARCHAR),
            new SqlParameter   ("p_salary",     Types.DECIMAL),
            new SqlParameter("p_job_type",   Types.VARCHAR),
            new SqlParameter   ("p_deadline",   Types.DATE),
            new SqlParameter   ("p_post_date",  Types.DATE),
            new SqlParameter   ("p_is_active",  Types.TINYINT),
            new SqlOutParameter("p_new_id",     Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_company_id",  companyId)
        .addValue("p_title",       title)
        .addValue("p_description", desc)
        .addValue("p_salary",      salary)
        .addValue("p_job_type",    jobType)
        .addValue("p_deadline",    deadline)
        .addValue("p_post_date",   postDate)
        .addValue("p_is_active",   active);

    Map<String,Object> out = insertProc.execute(in);
    Integer newId = (Integer) out.get("p_new_id");
    if (newId != null) {
      jobListing.setJobId(newId);
    } else {
      System.err.println("Warning: failed to retrieve new jobID");
    }

    return jobListing;
  }


  @Override
  public JobListing update(JobListing jobListing) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_joblisting_full")
        .declareParameters(
            new SqlParameter   ("p_job_id",       Types.INTEGER),
            new SqlParameter   ("p_company_id",   Types.INTEGER),
            new SqlParameter   ("p_title",        Types.VARCHAR),
            new SqlParameter   ("p_description",  Types.LONGVARCHAR),
            new SqlParameter   ("p_salary",       Types.DECIMAL),
            new SqlParameter   ("p_job_type",     Types.VARCHAR),
            new SqlParameter   ("p_deadline",     Types.DATE),
            new SqlParameter   ("p_post_date",    Types.DATE),
            new SqlParameter   ("p_is_active",    Types.TINYINT),
            new SqlOutParameter("p_rows_updated", Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_job_id",       jobListing.getJobId())
        .addValue("p_company_id",   jobListing.getCompany().getCompanyId())
        .addValue("p_title",        jobListing.getTitle())
        .addValue("p_description",  jobListing.getDescription())
        .addValue("p_salary",       jobListing.getSalary())
        .addValue("p_job_type",     jobListing.getJobType().toString())
        .addValue("p_deadline",     Date.valueOf(jobListing.getDeadline()))
        .addValue("p_post_date",    Date.valueOf(jobListing.getPostDate()))
        .addValue("p_is_active",    jobListing.getActive());

    Map<String, Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no joblisting row updated for jobID="
          + jobListing.getJobId());
    }

    return jobListing;
  }

  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_joblisting_by_id")
          .declareParameters(
              new SqlParameter   ("p_job_id",       Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_job_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_delete_joblisting_by_id failed: " + e.getMessage());
      return false;
    }
  }

  @Override
  public Optional<JobListing> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_joblisting_by_id")
          .returningResultSet("rs", (rs, rowNum) -> {
            JobListing j = new JobListing();
            j.setJobId(rs.getInt("jobID"));
            j.setTitle(rs.getString("title"));
            j.setDescription(rs.getString("description"));
            j.setSalary(rs.getBigDecimal("salary"));

            String jobTypeStr = rs.getString("jobType");
            if (jobTypeStr != null) {
              j.setJobType(JobListing.JobType.valueOf(jobTypeStr));
            }

            j.setDeadline(rs.getDate("deadline").toLocalDate());
            j.setPostDate(rs.getDate("postDate").toLocalDate());
            j.setActive(rs.getBoolean("isActive"));

            Company company = new Company();
            company.setCompanyId(rs.getInt("companyID"));
            j.setCompany(company);

            // Load full company details
            companyRepository.findById(company.getCompanyId())
                .ifPresent(j::setCompany);

            return j;
          });

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_job_id", id);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<JobListing> list = (List<JobListing>) out.get("rs");
      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("sp_get_joblisting_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public List<JobListing> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_joblistings")
          .returningResultSet("rs", jobListingRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<JobListing> list = (List<JobListing>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_joblistings failed: " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public List<JobListing> getJobsByRecruiterId(Integer recruiterId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_jobs_by_recruiter_id")
          .returningResultSet("rs", jobListingRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_recruiter_id", recruiterId);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<JobListing> jobs = (List<JobListing>) out.get("rs");
      return jobs;

    } catch (Exception e) {
      System.err.println("sp_get_jobs_by_recruiter_id failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<JobListing> findActiveJobs() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_active_joblistings")
          .returningResultSet("rs", jobListingRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<JobListing> activeJobs = (List<JobListing>) out.get("rs");
      return activeJobs;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_active_joblistings failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<JobListing> findByJobType(JobListing.JobType jobType) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_joblistings_by_type")
          .returningResultSet("rs", jobListingRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_job_type", jobType.toString());

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<JobListing> list = (List<JobListing>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: use proper logging
      System.err.println("sp_get_joblistings_by_type failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<JobListing> findByDeadlineNotPassed() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_joblistings_by_deadline_not_passed")
          .returningResultSet("rs", jobListingRowMapper);

      Map<String,Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<JobListing> list = (List<JobListing>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_joblistings_by_deadline_not_passed failed: " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public List<JobListing> findByTitleContaining(String title) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_find_joblisting_by_title_pattern")
          .returningResultSet("rs", jobListingRowMapper);

      // build the wildcard pattern
      String pattern = "%" + title + "%";

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_pattern", pattern);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<JobListing> list = (List<JobListing>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: use proper logging
      System.err.println("sp_find_joblisting_by_title_pattern failed: " + e.getMessage());
      return List.of();
    }
  }

}