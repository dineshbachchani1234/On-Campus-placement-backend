package com.campus.service;

import com.campus.model.Certification;
import com.campus.model.Skill;
import com.campus.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
  void createStudent(Student student);
  Optional<Student> getStudentById(int id);
  List<Student> getAllStudents();
  void updateStudent(Student student);
  void deleteStudent(int id);

  // --- Skill Management ---
  List<Skill> getSkillsByStudentId(Integer studentId);
  Skill addSkillToStudent(Integer studentId, String skillName);
  void removeSkillFromStudent(Integer studentId, Integer skillId);

  // --- Certification Management ---
  List<Certification> getCertificationsByStudentId(Integer studentId);
  // Pass individual fields instead of Certification object
  Certification addCertificationToStudent(Integer studentId, String name, String issuingOrg, java.sql.Date certDate, java.sql.Date expiryDate, String credentialId);
  void removeCertificationFromStudent(Integer studentId, Integer certificationId);

  // --- Applicant Management ---
  /**
   * Retrieves the details of students who have applied for a specific job.
   * @param jobId The ID of the job.
   * @return A list of Student objects representing the applicants.
   */
  List<Student> getApplicantsForJob(Integer jobId);
}
