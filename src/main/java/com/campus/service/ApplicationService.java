package com.campus.service;

import com.campus.model.Application;

import java.util.List;
import java.util.Optional;

/**
 * Service for job application operations
 */
public interface ApplicationService {

  /**
   * Submit a job application
   * @param application The application to submit
   * @return The submitted application
   */
  Application submitApplication(Application application);

  /**
   * Get application by ID
   * @param id The application ID
   * @return Optional containing the application if found
   */
  Optional<Application> getApplicationById(Integer id);


  /**
   * Get applications by student ID
   * @param studentId The student ID
   * @return List of applications submitted by the student
   */
  List<Application> getApplicationsByStudentId(Integer studentId);

  /**
   * Get applications for a job
   * @param jobId The job ID
   * @return List of applications for the job
   */
  List<Application> getApplicationsByJobId(Integer jobId);

  /**
   * Get applications by status
   * @param status The application status
   * @return List of applications with the specified status
   */
  List<Application> getApplicationsByStatus(Application.ApplicationStatus status);

  /**
   * Update application status
   * @param applicationId The application ID
   * @param status The new status
   * @return true if updated, false otherwise
   */
  boolean updateApplicationStatus(Integer applicationId, Application.ApplicationStatus status);

  /**
   * Check if a student has already applied to a job
   * @param studentId The student ID
   * @param jobId The job ID
   * @return true if an application exists, false otherwise
   */
  boolean hasStudentAppliedToJob(Integer studentId, Integer jobId);

  /**
   * Withdraw application
   * @param applicationId The application ID
   * @return true if withdrawn, false otherwise
   */
  boolean withdrawApplication(Integer applicationId);
}