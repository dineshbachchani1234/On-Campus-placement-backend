package com.campus.service;

import com.campus.model.Interview;
import com.campus.model.InterviewExperience;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for interview operations
 */
public interface InterviewService {

  /**
   * Schedule a new interview
   * @param interview The interview to schedule
   * @return The scheduled interview
   */
  Interview scheduleInterview(Interview interview);

  /**
   * Get interview by ID
   * @param id The interview ID
   * @return Optional containing the interview if found
   */
  Optional<Interview> getInterviewById(Integer id);

  /**
   * Get interviews by application ID
   * @param applicationId The application ID
   * @return List of interviews for the application
   */
  List<Interview> getInterviewsByApplicationId(Integer applicationId);

  /**
   * Get interviews by recruiter ID
   * @param recruiterId The recruiter ID
   * @return List of interviews conducted by the recruiter
   */
  List<Interview> getInterviewsByRecruiterId(Integer recruiterId);

  /**
   * Get interviews by status
   * @param status The interview status
   * @return List of interviews with the specified status
   */
  List<Interview> getInterviewsByStatus(Interview.InterviewStatus status);

  /**
   * Get interviews by result
   * @param result The interview result
   * @return List of interviews with the specified result
   */
  List<Interview> getInterviewsByResult(Interview.InterviewResult result);

  /**
   * Get upcoming interviews
   * @return List of upcoming interviews
   */
  List<Interview> getUpcomingInterviews();

  /**
   * Update interview status
   * @param interviewId The interview ID
   * @param status The new status
   * @return true if updated, false otherwise
   */
  boolean updateInterviewStatus(Integer interviewId, Interview.InterviewStatus status);

  /**
   * Update interview result and feedback
   * @param interviewId The interview ID
   * @param result The new result
   * @param feedback The feedback
   * @return true if updated, false otherwise
   */
  boolean updateInterviewResult(Integer interviewId, Interview.InterviewResult result, String feedback);

  /**
   * Add interview experience
   * @param experience The interview experience to add
   * @return The added interview experience
   */
  InterviewExperience addInterviewExperience(InterviewExperience experience);

  /**
   * Get experiences for an interview
   * @param interviewId The interview ID
   * @return List of experiences for the interview
   */
  List<InterviewExperience> getExperiencesByInterviewId(Integer interviewId);

  /**
   * Cancel interview
   * @param interviewId The interview ID
   * @return true if cancelled, false otherwise
   */
  boolean cancelInterview(Integer interviewId);

  /**
   * Reschedule interview
   * @param interviewId The interview ID
   * @param newDateTime The new date and time
   * @return true if rescheduled, false otherwise
   */
  boolean rescheduleInterview(Integer interviewId, LocalDateTime newDateTime);
}