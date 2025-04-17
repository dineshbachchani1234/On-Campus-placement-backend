package com.campus.repository;

import com.campus.model.Certification;
import com.campus.model.Skill;
import com.campus.model.Student;

import java.sql.Date;
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

  /**
   * Find students by a list of their IDs.
   * @param ids List of student IDs.
   * @return List of students matching the provided IDs.
   */
  List<Student> findByIdIn(List<Integer> ids);

  // --- Skill Methods ---

  /**
   * Find skills associated with a student.
   * @param studentId The student ID.
   * @return List of skills.
   */
  List<Skill> findSkillsByStudentId(Integer studentId);

  /**
   * Add a skill to a student, finding or creating the skill if necessary.
   * @param studentId The student ID.
   * @param skillName The name of the skill.
   * @return The Skill object that was linked (either found or created).
   */
  Skill addSkillToStudent(Integer studentId, String skillName);

  /**
   * Remove a skill association from a student.
   * @param studentId The student ID.
   * @param skillId The skill ID.
   */
  void removeSkillFromStudent(Integer studentId, Integer skillId);

  // --- Certification Methods ---

  /**
   * Find certifications associated with a student.
   * @param studentId The student ID.
   * @return List of certifications.
   */
  List<Certification> findCertificationsByStudentId(Integer studentId);

  /**
   * Add a certification to a student. Assumes the underlying stored procedure
   * handles finding/creating the base Certification record if needed.
   * @param studentId The student ID.
   * @param certification The Certification object containing details (name, issuingOrg).
   * @param certDate The date the certification was obtained.
   * @param expiryDate Optional expiry date.
   * @param credentialId Optional (but will be generated if null/empty by service) credential ID.
   * @return The Certification object linked to the student.
   */
  // Keep Certification object here for name/issuingOrg, but add credentialId explicitly
  Certification addCertificationToStudent(Integer studentId, Certification certInfo, Date certDate, Date expiryDate, String credentialId);

  /**
   * Remove a certification association from a student.
   * @param studentId The student ID.
   * @param certificationId The certification ID.
   */
  void removeCertificationFromStudent(Integer studentId, Integer certificationId);
}
