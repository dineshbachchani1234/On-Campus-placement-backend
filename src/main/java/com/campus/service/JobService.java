package com.campus.service;

import com.campus.model.Application;
import com.campus.model.JobListing;

import java.util.List;
import java.util.Optional;

/**
 * Service for job operations
 */
public interface JobService {

  /**
   * Create a new job listing
   * @param jobListing The job listing to create
   * @return The created job listing
   */
  JobListing createJob(JobListing jobListing);

  /**
   * Get job listing by ID
   * @param id The job ID
   * @return Optional containing the job listing if found
   */
  Optional<JobListing> getJobById(Integer id);

  /**
   * Get all job listings
   * @return List of all job listings
   */
  List<JobListing> getAllJobs();

  /**
   * Get job listings by company ID
   * @param companyId The company ID
   * @return List of job listings from the company
   */
  List<JobListing> getJobsByRecruiterId(Integer companyId);
  /**
   * Get active job listings
   * @return List of active job listings
   */
  List<JobListing> getActiveJobs();

  /**
   * Get job listings by job type
   * @param jobType The job type
   * @return List of job listings with the specified type
   */
  List<JobListing> getJobsByJobType(JobListing.JobType jobType);

  /**
   * Get job listings with future deadlines
   * @return List of job listings with future deadlines
   */
  List<JobListing> getJobsWithFutureDeadlines();

  /**
   * Search job listings by title
   * @param title The title to search for
   * @return List of job listings with titles containing the search term
   */
  List<JobListing> searchJobsByTitle(String title);

  /**
   * Update job listing
   * @param jobListing The job listing to update
   * @return The updated job listing
   */
  JobListing updateJob(JobListing jobListing);

  /**
   * Delete job listing
   * @param id The job ID
   * @return true if deleted, false otherwise
   */
  boolean deleteJob(Integer id);

  /**
   * Activate job listing
   * @param id The job ID
   * @return true if activated, false otherwise
   */
  boolean activateJob(Integer id);

  /**
   * Deactivate job listing
   * @param id The job ID
   * @return true if deactivated, false otherwise
   */
  boolean deactivateJob(Integer id);

  /**
   * Get relevant jobs for a student
   * @param studentId The student ID
   * @return List of relevant job listings
   */
  List<JobListing> getRelevantJobsForStudent(Integer studentId);
}