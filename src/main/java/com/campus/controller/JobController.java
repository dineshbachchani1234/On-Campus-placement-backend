package com.campus.controller;

import com.campus.model.JobListing;
import com.campus.model.MessageResponse;
import com.campus.service.JobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for job operations
 */
@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

  @Autowired
  private JobService jobService;

  /**
   * Create a new job listing
   * @param jobListing The job listing to create
   * @return Created job listing
   */
  @PostMapping
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> createJob( @RequestBody JobListing jobListing) {
    try {
      JobListing createdJob = jobService.createJob(jobListing);
      return ResponseEntity.ok(createdJob);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  /**
   * Get job listing by ID
   * @param id The job ID
   * @return Job listing if found
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getJobById(@PathVariable Integer id) {
    Optional<JobListing> job = jobService.getJobById(id);

    if (job.isPresent()) {
      return ResponseEntity.ok(job.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get all job listings
   * @return List of all job listings
   */
  @GetMapping
  public ResponseEntity<List<JobListing>> getAllJobs() {
    List<JobListing> jobs = jobService.getAllJobs();
    return ResponseEntity.ok(jobs);
  }

  /**
   * Get job listings by company ID
   * @param recruiterId The company ID
   * @return List of job listings from the company
   */
  @GetMapping("/recruiter/{recruiterId}")
  public ResponseEntity<List<JobListing>> getJobsByRecruiterId(@PathVariable Integer recruiterId) {
    List<JobListing> jobs = jobService.getJobsByRecruiterId(recruiterId);
    return ResponseEntity.ok(jobs);
  }

  /**
   * Get active job listings
   * @return List of active job listings
   */
  @GetMapping("/active")
  public ResponseEntity<List<JobListing>> getActiveJobs() {
    List<JobListing> jobs = jobService.getActiveJobs();
    return ResponseEntity.ok(jobs);
  }

  /**
   * Get job listings by job type
   * @param jobType The job type
   * @return List of job listings with the specified type
   */
  @GetMapping("/type/{jobType}")
  public ResponseEntity<List<JobListing>> getJobsByJobType(@PathVariable JobListing.JobType jobType) {
    List<JobListing> jobs = jobService.getJobsByJobType(jobType);
    return ResponseEntity.ok(jobs);
  }

  /**
   * Get job listings with future deadlines
   * @return List of job listings with future deadlines
   */
  @GetMapping("/available")
  public ResponseEntity<List<JobListing>> getJobsWithFutureDeadlines() {
    List<JobListing> jobs = jobService.getJobsWithFutureDeadlines();
    return ResponseEntity.ok(jobs);
  }

  /**
   * Search job listings by title
   * @param title The title to search for
   * @return List of job listings with titles containing the search term
   */
  @GetMapping("/search")
  public ResponseEntity<List<JobListing>> searchJobsByTitle(@RequestParam String title) {
    List<JobListing> jobs = jobService.searchJobsByTitle(title);
    return ResponseEntity.ok(jobs);
  }

  /**
   * Update job listing
   * @param id The job ID
   * @param jobListing The updated job data
   * @return Updated job listing
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> updateJob(@PathVariable Integer id,  @RequestBody JobListing jobListing) {
    // Ensure ID consistency
//    if (!id.equals(jobListing.getJobId())) {
//      return ResponseEntity.badRequest()
//          .body(MessageResponse.error("ID in path variable doesn't match ID in request body"));
//    }
    jobListing.setJobId(id);

    try {
      JobListing updatedJob = jobService.updateJob(jobListing);
      return ResponseEntity.ok(updatedJob);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  /**
   * Delete job listing
   * @param id The job ID
   * @return Success message
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> deleteJob(@PathVariable Integer id) {
    boolean deleted = jobService.deleteJob(id);

    if (deleted) {
      return ResponseEntity.ok(MessageResponse.success("Job deleted successfully"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Activate job listing
   * @param id The job ID
   * @return Success message
   */
  @PutMapping("/{id}/activate")
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> activateJob(@PathVariable Integer id) {
    boolean activated = jobService.activateJob(id);

    if (activated) {
      return ResponseEntity.ok(MessageResponse.success("Job activated successfully"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Deactivate job listing
   * @param id The job ID
   * @return Success message
   */
  @PutMapping("/{id}/deactivate")
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> deactivateJob(@PathVariable Integer id) {
    boolean deactivated = jobService.deactivateJob(id);

    if (deactivated) {
      return ResponseEntity.ok(MessageResponse.success("Job deactivated successfully"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get relevant jobs for a student
   * @param studentId The student ID
   * @return List of relevant job listings
   */
  @GetMapping("/relevant/{studentId}")
  public ResponseEntity<List<JobListing>> getRelevantJobsForStudent(@PathVariable Integer studentId) {
    List<JobListing> jobs = jobService.getRelevantJobsForStudent(studentId);
    return ResponseEntity.ok(jobs);
  }
}