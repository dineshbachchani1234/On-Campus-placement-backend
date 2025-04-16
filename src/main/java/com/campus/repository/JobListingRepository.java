package com.campus.repository;


import com.campus.model.Application;
import com.campus.model.JobListing;

import java.util.List;

public interface JobListingRepository extends BaseRepository<JobListing, Integer> {
  /**
   * Find jobs by company ID
   * @param companyId The company ID
   * @return List of jobs from the company
   */
  List<JobListing> getJobsByRecruiterId(Integer companyId);

  /**
   * Find active jobs
   * @return List of active jobs
   */
  List<JobListing> findActiveJobs();

  /**
   * Find jobs by job type
   * @param jobType The job type
   * @return List of jobs with the specified type
   */
  List<JobListing> findByJobType(JobListing.JobType jobType);

  /**
   * Find jobs where the application deadline has not passed
   * @return List of jobs with future deadlines
   */
  List<JobListing> findByDeadlineNotPassed();

  /**
   * Find jobs by title (partial match)
   * @param title The title to search for
   * @return List of jobs with titles containing the search term
   */
  List<JobListing> findByTitleContaining(String title);
}