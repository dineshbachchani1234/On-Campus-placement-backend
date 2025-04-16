package com.campus.repository;


import com.campus.model.Application;
import com.campus.model.Company;
import com.campus.model.JobListing;

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
    String sql = "INSERT INTO joblisting (companyID, title, description, salary, jobType, deadline, " +
        "postDate, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, jobListing.getCompany().getCompanyId());
      ps.setString(2, jobListing.getTitle());
      ps.setString(3, jobListing.getDescription());
      ps.setBigDecimal(4, jobListing.getSalary());
      ps.setString(5, jobListing.getJobType().toString());
      ps.setDate(6, Date.valueOf(jobListing.getDeadline()));
      ps.setDate(7, jobListing.getPostDate() != null ?
          Date.valueOf(jobListing.getPostDate()) :
          Date.valueOf(LocalDate.now()));
      ps.setBoolean(8, jobListing.getActive() != null ?
          jobListing.getActive() : true);
      return ps;
    }, keyHolder);

    jobListing.setJobId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    return jobListing;
  }

  @Override
  public JobListing update(JobListing jobListing) {
    String sql = "UPDATE joblisting SET companyID = ?, title = ?, description = ?, salary = ?, " +
        "jobType = ?, deadline = ?, postDate = ?, isActive = ? WHERE jobID = ?";

    jdbcTemplate.update(sql,
        jobListing.getCompany().getCompanyId(),
        jobListing.getTitle(),
        jobListing.getDescription(),
        jobListing.getSalary(),
        jobListing.getJobType().toString(),
        Date.valueOf(jobListing.getDeadline()),
        Date.valueOf(jobListing.getPostDate()),
        jobListing.getActive(),
        jobListing.getJobId()
    );

    return jobListing;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM joblisting WHERE jobID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<JobListing> findById(Integer id) {
    String sql = "SELECT * FROM joblisting WHERE jobID = ?";

    try {
      JobListing job = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
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

        // Load the full company details
        companyRepository.findById(company.getCompanyId()).ifPresent(j::setCompany);

        return j;
      }, id);

      return Optional.ofNullable(job);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<JobListing> findAll() {
    String sql = "SELECT * FROM joblisting";
    return jdbcTemplate.query(sql, jobListingRowMapper);
  }

  @Override
  public List<JobListing> getJobsByRecruiterId(Integer companyId) {
    String sql = "SELECT j.* FROM joblisting j\n"
        + "                 INNER JOIN company c ON j.companyID = c.companyID\n"
        + "                 INNER JOIN recruiter r ON r.companyID = c.companyID\n"
        + "                 WHERE r.recruiterID = ?";
    return jdbcTemplate.query(sql, jobListingRowMapper, companyId);
  }

  @Override
  public List<JobListing> findActiveJobs() {
    String sql = "SELECT * FROM joblisting WHERE isActive = TRUE";
    return jdbcTemplate.query(sql, jobListingRowMapper);
  }

  @Override
  public List<JobListing> findByJobType(JobListing.JobType jobType) {
    String sql = "SELECT * FROM joblisting WHERE jobType = ?";
    return jdbcTemplate.query(sql, jobListingRowMapper, jobType.toString());
  }

  @Override
  public List<JobListing> findByDeadlineNotPassed() {
    String sql = "SELECT * FROM joblisting WHERE deadline >= CURRENT_DATE()";
    return jdbcTemplate.query(sql, jobListingRowMapper);
  }

  @Override
  public List<JobListing> findByTitleContaining(String title) {
    String sql = "SELECT * FROM joblisting WHERE title LIKE ?";
    return jdbcTemplate.query(sql, jobListingRowMapper, "%" + title + "%");
  }
}