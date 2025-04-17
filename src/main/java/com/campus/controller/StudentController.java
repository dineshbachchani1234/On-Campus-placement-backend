package com.campus.controller;

import com.campus.model.Certification;
import com.campus.model.CertificationRequestDTO;
import com.campus.model.Skill;
import com.campus.model.SkillRequestDTO;
import com.campus.model.Student;
import com.campus.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date; // Import java.sql.Date
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

  @Autowired
  private StudentService studentService;

  @PostMapping
  public String createStudent(@RequestBody Student student) {
    studentService.createStudent(student);
    return "Student created successfully";
  }

  @GetMapping("/{id}")
  public Optional<Student> getStudentById(@PathVariable int id) {
    return studentService.getStudentById(id);
  }

  @GetMapping
  public List<Student> getAllStudents() {
    return studentService.getAllStudents();
  }

  @PutMapping("/{id}")
  public String updateStudent(@PathVariable int id, @RequestBody Student student) {
    student.setStudentId(id);
    studentService.updateStudent(student);
    return "Student updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteStudent(@PathVariable int id) {
    studentService.deleteStudent(id);
    // Consider returning ResponseEntity.noContent() for DELETE
    return "Student deleted successfully";
  }


  @GetMapping("/{id}/skills")
  public ResponseEntity<List<Skill>> getStudentSkills(@PathVariable("id") Integer studentId) {
      // Optional: Check if student exists first
      // studentService.getStudentById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
      List<Skill> skills = studentService.getSkillsByStudentId(studentId);
      return ResponseEntity.ok(skills);
  }

  @PostMapping("/{id}/skills")
  public ResponseEntity<Skill> addStudentSkill(@PathVariable("id") Integer studentId, @RequestBody SkillRequestDTO skillRequest) {
       // Optional: Check if student exists first
      try {
          Skill addedSkill = studentService.addSkillToStudent(studentId, skillRequest.getName());
          // Consider returning ResponseEntity.created() with location header if applicable
          return ResponseEntity.status(HttpStatus.CREATED).body(addedSkill);
      } catch (Exception e) {
          // Log exception
          // Consider more specific exception handling
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
  }

  @DeleteMapping("/{id}/skills/{skillId}")
  public ResponseEntity<Void> removeStudentSkill(@PathVariable("id") Integer studentId, @PathVariable Integer skillId) {
      // Optional: Check if student exists first
      try {
          studentService.removeSkillFromStudent(studentId, skillId);
          return ResponseEntity.noContent().build();
      } catch (Exception e) {
          // Log exception
          // Consider returning 404 if the link doesn't exist, based on service/repo behavior
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
  }


  // --- Student Certifications Endpoints ---

  @GetMapping("/{id}/certifications")
  public ResponseEntity<List<Certification>> getStudentCertifications(@PathVariable("id") Integer studentId) {
       // Optional: Check if student exists first
      List<Certification> certifications = studentService.getCertificationsByStudentId(studentId);
      return ResponseEntity.ok(certifications);
  }

  @PostMapping("/{id}/certifications")
  public ResponseEntity<Certification> addStudentCertification(@PathVariable("id") Integer studentId, @RequestBody CertificationRequestDTO certRequest) {
      // Optional: Check if student exists first
      try {
          // --- Date Parsing ---
          Date sqlCertDate = null;
          Date sqlExpiryDate = null;

          if (certRequest.getCertificationDate() != null && !certRequest.getCertificationDate().isEmpty()) {
              try {
                  sqlCertDate = Date.valueOf(certRequest.getCertificationDate()); // Assumes YYYY-MM-DD format
              } catch (IllegalArgumentException dateEx) {
                  // Handle invalid date format from DTO
                  return ResponseEntity.badRequest().body(null); // Or return specific error message
              }
          } else {
               // Handle case where mandatory date is missing
               return ResponseEntity.badRequest().body(null); // Or return specific error message
          }

          if (certRequest.getExpiryDate() != null && !certRequest.getExpiryDate().isEmpty()) {
               try {
                  sqlExpiryDate = Date.valueOf(certRequest.getExpiryDate()); // Assumes YYYY-MM-DD format
               } catch (IllegalArgumentException dateEx) {
                  // Handle invalid date format from DTO
                  return ResponseEntity.badRequest().body(null); // Or return specific error message
               }
          }
          // --- End Date Parsing ---

          // Ensure we are getting the String value from the DTO
          String credentialIdFromRequest = String.valueOf(certRequest.getCredentialId());

          Certification addedCertification = studentService.addCertificationToStudent(
              studentId,
              certRequest.getName(),
              certRequest.getIssuingOrganization(),
              sqlCertDate,
              sqlExpiryDate, // Pass null if not provided/parsed
              credentialIdFromRequest // Pass the String value explicitly
          );
          return ResponseEntity.status(HttpStatus.CREATED).body(addedCertification);
      } catch (IllegalArgumentException e) {
           // Catch validation errors from service
           return ResponseEntity.badRequest().body(null); // Or return error message from e.getMessage()
      }
       catch (Exception e) {
          // Log exception
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
  }

  @DeleteMapping("/{id}/certifications/{certificationId}")
  public ResponseEntity<Void> removeStudentCertification(@PathVariable("id") Integer studentId, @PathVariable Integer certificationId) {
       // Optional: Check if student exists first
       try {
          studentService.removeCertificationFromStudent(studentId, certificationId);
          return ResponseEntity.noContent().build();
      } catch (Exception e) {
          // Log exception
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
  }

}
