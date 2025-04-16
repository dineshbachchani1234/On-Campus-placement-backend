package com.campus.repository;

import com.campus.model.Interview;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewRepository extends BaseRepository<Interview, Integer> {
  /**
   * Find interviews by application ID
   * @param applicationId The application ID
   * @return List of interviews for the application
   */
  List<Interview> findByApplicationId(Integer applicationId);

  /**
   * Find interviews by recruiter ID
   * @param recruiterId The recruiter ID
   * @return List of interviews conducted by the recruiter
   */
  List<Interview> findByRecruiterId(Integer recruiterId);

  /**
   * Find interviews by status
   * @param status The interview status
   * @return List of interviews with the specified status
   */
  List<Interview> findByStatus(Interview.InterviewStatus status);

  /**
   * Find interviews by result
   * @param result The interview result
   * @return List of interviews with the specified result
   */
  List<Interview> findByResult(Interview.InterviewResult result);

  /**
   * Find upcoming interviews
   * @param date The date to compare with
   * @return List of interviews scheduled after the specified date
   */
  List<Interview> findUpcomingInterviews(LocalDateTime date);

  /**
   * Update interview status
   * @param interviewId The interview ID
   * @param status The new status
   * @return true if updated, false otherwise
   */
  boolean updateStatus(Integer interviewId, Interview.InterviewStatus status);

  /**
   * Update interview result
   * @param interviewId The interview ID
   * @param result The new result
   * @param feedback The feedback
   * @return true if updated, false otherwise
   */
  boolean updateResult(Integer interviewId, Interview.InterviewResult result, String feedback);
}