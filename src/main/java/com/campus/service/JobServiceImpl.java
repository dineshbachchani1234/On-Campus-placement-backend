package com.campus.service;

import com.campus.model.Application;
import com.campus.model.JobListing;
import com.campus.repository.JobListingRepository;
import com.campus.service.JobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

  @Autowired
  private JobListingRepository jobListingRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public JobListing createJob(JobListing jobListing) {
    // Set default values if not provided
    if (jobListing.getPostDate() == null) {
      jobListing.setPostDate(LocalDate.now());
    }

    if (jobListing.getActive() == null) {
      jobListing.setActive(true);
    }

    return jobListingRepository.save(jobListing);
  }

  @Override
  public Optional<JobListing> getJobById(Integer id) {
    return jobListingRepository.findById(id);
  }

  @Override
  public List<JobListing> getAllJobs() {
    return jobListingRepository.findAll();
  }

  @Override
  public List<JobListing> getJobsByRecruiterId(Integer recruiterId) {
    return jobListingRepository.getJobsByRecruiterId(recruiterId);
  }

  @Override
  public List<JobListing> getActiveJobs() {
    return jobListingRepository.findActiveJobs();
  }

  @Override
  public List<JobListing> getJobsByJobType(JobListing.JobType jobType) {
    return jobListingRepository.findByJobType(jobType);
  }

  @Override
  public List<JobListing> getJobsWithFutureDeadlines() {
    return jobListingRepository.findByDeadlineNotPassed();
  }

  @Override
  public List<JobListing> searchJobsByTitle(String title) {
    return jobListingRepository.findByTitleContaining(title);
  }

  @Override
  public JobListing updateJob(JobListing jobListing) {
    // Check if job exists
    Optional<JobListing> existingJob = jobListingRepository.findById(jobListing.getJobId());
    if (!existingJob.isPresent()) {
      throw new RuntimeException("Job not found");
    }

    return jobListingRepository.update(jobListing);
  }

  @Override
  public boolean deleteJob(Integer id) {
    return jobListingRepository.deleteById(id);
  }

  @Override
  public boolean activateJob(Integer id) {
    Optional<JobListing> jobOptional = jobListingRepository.findById(id);
    if (!jobOptional.isPresent()) {
      return false;
    }

    JobListing job = jobOptional.get();
    job.setActive(true);
    jobListingRepository.update(job);

    return true;
  }

  @Override
  public boolean deactivateJob(Integer id) {
    Optional<JobListing> jobOptional = jobListingRepository.findById(id);
    if (!jobOptional.isPresent()) {
      return false;
    }

    JobListing job = jobOptional.get();
    job.setActive(false);
    jobListingRepository.update(job);

    return true;
  }

  @Override
  public List<JobListing> getRelevantJobsForStudent(Integer studentId) {
    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("get_relevant_jobs_for_student")
        .declareParameters(new SqlParameter("student_id", Types.INTEGER))
        .returningResultSet("jobListings", BeanPropertyRowMapper.newInstance(JobListing.class)); // Assuming column names match JobListing properties

    SqlParameterSource in = new MapSqlParameterSource().addValue("student_id", studentId);
    Map<String, Object> out = jdbcCall.execute(in);

    // The result set is typically returned under the key specified in returningResultSet
    @SuppressWarnings("unchecked")
    List<JobListing> jobs = (List<JobListing>) out.get("jobListings");
    return jobs;
  }
}
