package com.campus.repository;

import com.campus.model.InterviewExperience;

import java.util.List;

/**
 * Repository for interview experience operations
 */
public interface InterviewExperienceRepository extends BaseRepository<InterviewExperience, Integer> {

  /**
   * Find experiences by interview ID
   * @param interviewId The interview ID
   * @return List of experiences for the interview
   */
  List<InterviewExperience> findByInterviewId(Integer interviewId);

  /**
   * Find experiences by student ID
   * @param studentId The student ID
   * @return List of experiences shared by the student
   */
  List<InterviewExperience> findByStudentId(Integer studentId);

  List<InterviewExperience> findAllExperiences();

}