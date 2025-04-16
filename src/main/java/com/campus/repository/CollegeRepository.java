package com.campus.repository;

import com.campus.model.College;

import java.util.Optional;

public interface CollegeRepository extends BaseRepository<College, Integer> {
  /**
   * Find a college by name
   * @param name The college name
   * @return Optional containing the college if found
   */
  Optional<College> findByName(String name);

  /**
   * Check if a college name already exists
   * @param name The college name to check
   * @return true if the college name exists, false otherwise
   */
  boolean existsByName(String name);

  /**
   * Update the number of students in a college
   * @param collegeId The college ID
   * @param numberOfStudents The new number of students
   * @return true if updated, false otherwise
   */
  boolean updateNumberOfStudents(Integer collegeId, Integer numberOfStudents);
}