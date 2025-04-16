package com.campus.repository;

import com.campus.model.Student;
import java.util.List;

public interface StudentRepository extends BaseRepository<Student, Integer> {
  /**
   * Find students by college ID
   * @param collegeId The college ID
   * @return List of students in the college
   */
  List<Student> findByCollegeId(Integer collegeId);

  /**
   * Find placed students
   * @return List of placed students
   */
  List<Student> findPlacedStudents();

  /**
   * Find students by major
   * @param major The major to search for
   * @return List of students with the major
   */
  List<Student> findByMajor(String major);

  /**
   * Find students with GPA greater than or equal to the specified value
   * @param gpa The minimum GPA
   * @return List of students with GPA >= the specified value
   */
  List<Student> findByGpaGreaterThanEqual(double gpa);
}