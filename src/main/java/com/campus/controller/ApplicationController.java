package com.campus.controller;

import com.campus.model.Application;
import com.campus.model.MessageResponse;
import com.campus.service.ApplicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for job application operations
 */
@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

  @Autowired
  private ApplicationService applicationService;

  /**
   * Submit a job application
   * @param application The application to submit
   * @return Submitted application
   */
  @PostMapping
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<?> submitApplication( @RequestBody Application application) {
    try {
      Application submittedApplication = applicationService.submitApplication(application);
      return ResponseEntity.ok(submittedApplication);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  /**
   * Get application by ID
   * @param id The application ID
   * @return Application if found
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getApplicationById(@PathVariable Integer id) {
    Optional<Application> application = applicationService.getApplicationById(id);

    if (application.isPresent()) {
      return ResponseEntity.ok(application.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get applications by student ID
   * @param studentId The student ID
   * @return List of applications submitted by the student
   */
  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<Application>> getApplicationsByStudentId(@PathVariable Integer studentId) {
    List<Application> applications = applicationService.getApplicationsByStudentId(studentId);
    return ResponseEntity.ok(applications);
  }

  /**
   * Get applications for a job
   * @param jobId The job ID
   * @return List of applications for the job
   */
  @GetMapping("/job/{jobId}")
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<List<Application>> getApplicationsByJobId(@PathVariable Integer jobId) {
    List<Application> applications = applicationService.getApplicationsByJobId(jobId);
    return ResponseEntity.ok(applications);
  }

  /**
   * Get applications by status
   * @param status The application status
   * @return List of applications with the specified status
   */
  @GetMapping("/status/{status}")
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<List<Application>> getApplicationsByStatus(@PathVariable Application.ApplicationStatus status) {
    List<Application> applications = applicationService.getApplicationsByStatus(status);
    return ResponseEntity.ok(applications);
  }

  /**
   * Update application status
   * @param id The application ID
   * @param status The new status
   * @return Success message
   */
  @PutMapping("/{id}/status")
  @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> updateApplicationStatus(
      @PathVariable Integer id,
      @RequestParam Application.ApplicationStatus status) {

    boolean updated = applicationService.updateApplicationStatus(id, status);

    if (updated) {
      return ResponseEntity.ok(MessageResponse.success("Application status updated successfully"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Check if a student has already applied to a job
   * @param studentId The student ID
   * @param jobId The job ID
   * @return Boolean indicating if an application exists
   */
  @GetMapping("/check")
  public ResponseEntity<Boolean> hasStudentAppliedToJob(
      @RequestParam Integer studentId,
      @RequestParam Integer jobId) {

    boolean hasApplied = applicationService.hasStudentAppliedToJob(studentId, jobId);
    return ResponseEntity.ok(hasApplied);
  }

  /**
   * Withdraw application
   * @param id The application ID
   * @return Success message
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<?> withdrawApplication(@PathVariable Integer id) {
    boolean withdrawn = applicationService.withdrawApplication(id);

    if (withdrawn) {
      return ResponseEntity.ok(MessageResponse.success("Application withdrawn successfully"));
    } else {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error("Application cannot be withdrawn"));
    }
  }
}