package com.campus.repository;


import com.campus.model.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends BaseRepository<Application, Integer> {
  /**
   * Find applications by student ID
   * @param studentId The student ID
   * @return List of applications submitted by the student
   */
  List<Application> findByStudentId(Integer studentId);

  /**
   * Find applications for a job
   * @param jobId The job ID
   * @return List of applications for the job
   */
  List<Application> findByJobId(Integer jobId);

  /**
   * Find a specific application by job ID and student ID.
   * @param jobId The job ID.
   * @param studentId The student ID.
   * @return Optional containing the application if found.
   */
  Optional<Application> findByJobIdAndStudentId(Integer jobId, Integer studentId);



  /**
   * Find applications by status
   * @param status The application status
   * @return List of applications with the specified status
   */
  List<Application> findByStatus(Application.ApplicationStatus status);

  /**
   * Check if a student has already applied to a job
   * @param studentId The student ID
   * @param jobId The job ID
   * @return true if an application exists, false otherwise
   */
  boolean existsByStudentIdAndJobId(Integer studentId, Integer jobId);

  /**
   * Update application status
   * @param applicationId The application ID
   * @param status The new status
   * @return true if updated, false otherwise
   */
  boolean updateStatus(Integer applicationId, Application.ApplicationStatus status);
}
