package com.campus.service;

import com.campus.model.*; // Import all models including Application
import com.campus.repository.ApplicationRepository; // Added import
import com.campus.repository.StudentRepository;
// Potentially import other needed repositories if logic becomes complex, but for now StudentRepository handles it
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections; // Added import
import org.springframework.util.StringUtils; // Import StringUtils

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Import UUID
import java.util.stream.Collectors; // Added import

@Service
public class StudentServiceImpl implements StudentService {

  @Autowired
  private StudentRepository studentRepository;

  @Autowired // Added injection
  private ApplicationRepository applicationRepository;

  @Override
  public void createStudent(Student student) {
    studentRepository.save(student);
  }

  @Override
  public Optional<Student> getStudentById(int id) {
    return studentRepository.findById(id);
  }

  @Override
  public List<Student> getAllStudents() {
    return studentRepository.findAll();
  }

  @Override
  public void updateStudent(Student student) {
    studentRepository.update(student);
  }

  @Override
  public void deleteStudent(int id) {
    studentRepository.deleteById(id);
  }

  // --- Skill Management Implementation ---

  @Override
  public List<Skill> getSkillsByStudentId(Integer studentId) {
    // Basic validation could be added here (e.g., check if student exists)
    return studentRepository.findSkillsByStudentId(studentId);
  }

  @Override
  public Skill addSkillToStudent(Integer studentId, String skillName) {
    if (skillName == null || skillName.trim().isEmpty()) {
        throw new IllegalArgumentException("Skill name cannot be empty.");
    }
    // Further validation (e.g., check if student exists) could be added
    // The repository method handles finding/creating the skill and linking
    return studentRepository.addSkillToStudent(studentId, skillName.trim());
  }

  @Override
  public void removeSkillFromStudent(Integer studentId, Integer skillId) {
    // Validation (e.g., check if student/skill exists, or if the link exists) could be added
    studentRepository.removeSkillFromStudent(studentId, skillId);
  }

  // --- Certification Management Implementation ---

  @Override
  public List<Certification> getCertificationsByStudentId(Integer studentId) {
     // Basic validation could be added here
    return studentRepository.findCertificationsByStudentId(studentId);
  }

  @Override
  // Update signature to accept individual fields
  public Certification addCertificationToStudent(Integer studentId, String name, String issuingOrg, java.sql.Date certDate, java.sql.Date expiryDate, String credentialId) {
      // Validation
      if (!StringUtils.hasText(name) || !StringUtils.hasText(issuingOrg) || certDate == null) {
           throw new IllegalArgumentException("Certification name, issuing organization, and certification date are required.");
       }

       // Auto-generate credentialId if not provided
       String finalCredentialId = credentialId;
       if (!StringUtils.hasText(finalCredentialId)) {
           finalCredentialId = UUID.randomUUID().toString();
           System.out.println("Generated credentialId: " + finalCredentialId); // For debugging
       }

       // The repository method handles finding/creating the base certification and linking
      // Note: Date conversion (e.g., from String in DTO to java.sql.Date) should ideally happen
      // in the Controller before calling this service method. Assuming dates are passed correctly here.

      // Create a temporary Certification object just to pass name/issuingOrg easily to repo if needed by its signature
      // (Alternatively, update repo signature too)
      Certification certInfo = new Certification();
      certInfo.setName(name);
      certInfo.setIssuingOrganization(issuingOrg);


      // Call repository with individual fields
      return studentRepository.addCertificationToStudent(
          studentId,
          certInfo, // Pass object with name/org
          certDate,
          expiryDate,
          finalCredentialId // Pass the potentially generated credentialId
      );
  }

  @Override
  public void removeCertificationFromStudent(Integer studentId, Integer certificationId) {
      // Validation could be added
      studentRepository.removeCertificationFromStudent(studentId, certificationId);
  }

  // --- Applicant Management Implementation ---

  @Override
  public List<Student> getApplicantsForJob(Integer jobId) {
    // 1. Find all applications for the given job ID
    List<Application> applications = applicationRepository.findByJobId(jobId);

    if (applications.isEmpty()) {
      return Collections.emptyList(); // No applicants found
    }

    // 2. Extract the student IDs from the applications
    List<Integer> studentIds = applications.stream()
                                          .map(app -> app.getStudent().getStudentId())
                                          .distinct() // Ensure unique IDs
                                          .collect(Collectors.toList());

    if (studentIds.isEmpty()) {
        // Should not happen if applications list was not empty, but good practice
        return Collections.emptyList();
    }

    // 3. Fetch the student details using the collected IDs
    // This relies on the findByIdIn method implemented in StudentRepositoryImpl
    // and the associated stored procedure sp_get_students_by_ids
    List<Student> applicants = studentRepository.findByIdIn(studentIds);

    // Note: The studentRowMapper in StudentRepositoryImpl already handles fetching
    // the associated User details (firstName, lastName, email) via userRepository.findById.
    // So the returned Student objects should have the necessary info.

    return applicants;
  }
}
