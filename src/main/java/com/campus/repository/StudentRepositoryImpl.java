package com.campus.repository;

import com.campus.model.*; // Import all models
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class StudentRepositoryImpl implements StudentRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private UserRepository userRepository;

  private RowMapper<Student> studentRowMapper = (rs, rowNum) -> {
    Student student = new Student();
    student.setStudentId(rs.getInt("studentID"));

    College college = new College();
    college.setCollegeId(rs.getInt("collegeID"));
    student.setCollege(college);

    student.setMajor(rs.getString("major"));
    student.setGpa(rs.getBigDecimal("gpa"));
    student.setResume(rs.getString("resume"));
    student.setPlaced(rs.getBoolean("isPlaced"));
    student.setTotalApplicationsCount(rs.getInt("totalApplicationsCount"));

    // Load the user and set it on the student object
    userRepository.findById(student.getStudentId()).ifPresent(student::setUser);

    // Load College details (assuming CollegeRepository exists or is handled elsewhere)
    // Example: collegeRepository.findById(college.getCollegeId()).ifPresent(student::setCollege);
    // For now, we only have the ID from the student table join

    return student;
  };

  // RowMapper for Skill
  private RowMapper<Skill> skillRowMapper = (rs, rowNum) -> {
    Skill skill = new Skill();
    skill.setSkillId(rs.getInt("skillID"));
    skill.setSkillName(rs.getString("skillName"));
    skill.setDescription(rs.getString("description"));
    return skill;
  };

  // RowMapper for Certification (including student-specific details)
  private RowMapper<Certification> certificationRowMapper = (rs, rowNum) -> {
      Certification cert = new Certification();
      cert.setCertificationId(rs.getInt("certificationID"));
      cert.setName(rs.getString("name"));
      cert.setIssuingOrganization(rs.getString("issuingOrganization"));

      // Include details from the studentcertification join table if needed by the SP
      // Example (adjust based on your SP output):
      // cert.setCertificationDate(rs.getDate("certificationDate"));
      // cert.setExpiryDate(rs.getDate("expiryDate"));
      // cert.setCredentialId(rs.getString("credentialID"));
      return cert;
  };

  @Override
  public Student save(Student student) {
    SimpleJdbcCall insertProc = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_student")
        .declareParameters(
            new SqlParameter("p_student_id",         Types.INTEGER),
            new SqlParameter   ("p_college_id",         Types.INTEGER),
            new SqlParameter   ("p_major",              Types.VARCHAR),
            new SqlParameter   ("p_gpa",                Types.DECIMAL),
            new SqlParameter   ("p_resume",             Types.LONGVARCHAR),
            new SqlParameter   ("p_is_placed",          Types.TINYINT),
            new SqlParameter   ("p_total_applications", Types.INTEGER),
            new SqlOutParameter("p_rows_inserted",      Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_student_id",         student.getStudentId())
        .addValue("p_college_id",         student.getCollege().getCollegeId())
        .addValue("p_major",              student.getMajor())
        .addValue("p_gpa",                student.getGpa())
        .addValue("p_resume",             student.getResume())
        .addValue("p_is_placed",          student.getPlaced())
        .addValue("p_total_applications", student.getTotalApplicationsCount());

    Map<String,Object> out = insertProc.execute(in);
    Integer rows = (Integer) out.get("p_rows_inserted");
    if (rows == null || rows != 1) {
      System.err.println("Warning: expected 1 row inserted, got " + rows);
    }

    return student;
  }

  @Override
  public Student update(Student student) {
    SimpleJdbcCall updateProc = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_student")
        .declareParameters(
            new SqlParameter   ("p_student_id",         Types.INTEGER),
            new SqlParameter   ("p_college_id",         Types.INTEGER),
            new SqlParameter   ("p_major",              Types.VARCHAR),
            new SqlParameter   ("p_gpa",                Types.DECIMAL),
            new SqlParameter   ("p_resume",             Types.LONGVARCHAR),
            new SqlParameter   ("p_is_placed",          Types.TINYINT),
            new SqlParameter   ("p_total_applications", Types.INTEGER),
            new SqlOutParameter("p_rows_updated",       Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_student_id",         student.getStudentId())
        .addValue("p_college_id",         student.getCollege().getCollegeId())
        .addValue("p_major",              student.getMajor())
        .addValue("p_gpa",                student.getGpa())
        .addValue("p_resume",             student.getResume())
        .addValue("p_is_placed",          student.getPlaced())
        .addValue("p_total_applications", student.getTotalApplicationsCount());

    Map<String, Object> out = updateProc.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no student row updated for ID="
          + student.getStudentId());
    }

    return student;
  }


  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_student_by_id")
          .declareParameters(
              new SqlParameter   ("p_student_id",   Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_student_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_delete_student_by_id failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public Optional<Student> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_student_by_id")
          .returningResultSet("rs", studentRowMapper);

      // execute with IN param
      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_student_id", id);

      Map<String, Object> out = call.execute(in);

      @SuppressWarnings("unchecked")
      List<Student> list = (List<Student>) out.get("rs");
      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("sp_get_student_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public List<Student> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_students")
          .returningResultSet("rs", studentRowMapper);

      Map<String,Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Student> students = (List<Student>) out.get("rs");
      return students;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_students failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Student> findByCollegeId(Integer collegeId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_students_by_college_id")
          .returningResultSet("rs", studentRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_college_id", collegeId);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Student> students = (List<Student>) out.get("rs");
      return students;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_students_by_college_id failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Student> findPlacedStudents() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_placed_students")
          .returningResultSet("rs", studentRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Student> placed = (List<Student>) out.get("rs");
      return placed;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_placed_students failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Student> findByMajor(String major) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_students_by_major")
          .returningResultSet("rs", studentRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_major", major);

      Map<String,Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Student> students = (List<Student>) out.get("rs");
      return students;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_students_by_major failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Student> findByGpaGreaterThanEqual(double gpa) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_students_by_min_gpa")
          .returningResultSet("rs", studentRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_min_gpa", gpa);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Student> students = (List<Student>) out.get("rs");
      return students;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_students_by_min_gpa failed: " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public List<Student> findByIdIn(List<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of(); // Return empty list if input is empty
    }

    // Convert list of IDs to comma-separated string for the stored procedure
    String idListString = ids.stream()
                             .map(String::valueOf)
                             .collect(java.util.stream.Collectors.joining(","));

    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_students_by_ids") // Assumed SP name
          .returningResultSet("rs", studentRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_student_ids", idListString); // Pass comma-separated string

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Student> students = (List<Student>) out.get("rs");
      return students;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_students_by_ids failed: " + e.getMessage());
      return List.of();
    }
  }


  // --- Skill Methods Implementation ---

  @Override
  public List<Skill> findSkillsByStudentId(Integer studentId) {
      try {
          SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
              .withProcedureName("sp_get_student_skills") // Assumed SP name
              .returningResultSet("rs", skillRowMapper);

          MapSqlParameterSource in = new MapSqlParameterSource()
              .addValue("p_student_id", studentId);

          Map<String, Object> out = call.execute(in);
          @SuppressWarnings("unchecked")
          List<Skill> skills = (List<Skill>) out.get("rs");
          return skills;

      } catch (Exception e) {
          System.err.println("sp_get_student_skills failed for student " + studentId + ": " + e.getMessage());
          return List.of();
      }
  }

  @Override
  public Skill addSkillToStudent(Integer studentId, String skillName) {
      try {
          SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
              .withProcedureName("sp_find_or_create_skill_and_link_student") // Assumed SP name
              .declareParameters(
                  new SqlParameter("p_student_id", Types.INTEGER),
                  new SqlParameter("p_skill_name", Types.VARCHAR),
                  new SqlOutParameter("p_skill_id", Types.INTEGER), // SP should return the ID of the linked/created skill
                  new SqlOutParameter("p_skill_desc", Types.VARCHAR) // SP might return description too
              );

          MapSqlParameterSource in = new MapSqlParameterSource()
              .addValue("p_student_id", studentId)
              .addValue("p_skill_name", skillName);

          Map<String, Object> out = call.execute(in);
          Integer skillId = (Integer) out.get("p_skill_id");
          String skillDesc = (String) out.get("p_skill_desc");

          if (skillId != null) {
              Skill skill = new Skill();
              skill.setSkillId(skillId);
              skill.setSkillName(skillName);
              skill.setDescription(skillDesc); // Use description returned by SP if available
              return skill;
          } else {
              throw new RuntimeException("Failed to add or link skill '" + skillName + "' for student " + studentId);
          }

      } catch (Exception e) {
          System.err.println("sp_find_or_create_skill_and_link_student failed: " + e.getMessage());
          // Consider throwing a custom exception
          throw new RuntimeException("Error adding skill: " + e.getMessage(), e);
      }
  }

  @Override
  public void removeSkillFromStudent(Integer studentId, Integer skillId) {
      try {
          SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
              .withProcedureName("sp_remove_student_skill") // Assumed SP name
              .declareParameters(
                  new SqlParameter("p_student_id", Types.INTEGER),
                  new SqlParameter("p_skill_id", Types.INTEGER),
                  new SqlOutParameter("p_rows_deleted", Types.INTEGER)
              );

          MapSqlParameterSource in = new MapSqlParameterSource()
              .addValue("p_student_id", studentId)
              .addValue("p_skill_id", skillId);

          Map<String, Object> out = call.execute(in);
          Integer rows = (Integer) out.get("p_rows_deleted");
          if (rows == null || rows == 0) {
               System.err.println("Warning: No student_skill link found to delete for student " + studentId + ", skill " + skillId);
               // Optionally throw an exception if the link must exist
               // throw new RuntimeException("Skill link not found for deletion.");
          }
      } catch (Exception e) {
          System.err.println("sp_remove_student_skill failed: " + e.getMessage());
          throw new RuntimeException("Error removing skill link: " + e.getMessage(), e);
      }
  }

  // --- Certification Methods Implementation ---

  @Override
  public List<Certification> findCertificationsByStudentId(Integer studentId) {
      try {
          // This SP needs to join studentcertification and certification tables
          SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
              .withProcedureName("sp_get_student_certifications") // Assumed SP name
              .returningResultSet("rs", certificationRowMapper); // Use mapper for Certification

          MapSqlParameterSource in = new MapSqlParameterSource()
              .addValue("p_student_id", studentId);

          Map<String, Object> out = call.execute(in);
          @SuppressWarnings("unchecked")
          List<Certification> certifications = (List<Certification>) out.get("rs");
          return certifications;

      } catch (Exception e) {
          System.err.println("sp_get_student_certifications failed for student " + studentId + ": " + e.getMessage());
          return List.of();
      }
  }

  @Override
  // Update signature to match interface
  public Certification addCertificationToStudent(Integer studentId, Certification certInfo, Date certDate, Date expiryDate, String credentialId) {
       try {
           // This SP needs to handle finding/creating the base Certification record
           // and then inserting the link into studentcertification
           SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
               .withProcedureName("sp_add_student_certification") // Assumed SP name
               .declareParameters(
                   new SqlParameter("p_student_id", Types.INTEGER),
                   new SqlParameter("p_cert_name", Types.VARCHAR),
                   new SqlParameter("p_issuing_org", Types.VARCHAR), // Assuming these are needed to find/create base cert
                   new SqlParameter("p_cert_date", Types.DATE),
                   new SqlParameter("p_expiry_date", Types.DATE),
                   new SqlParameter("p_credential_id", Types.VARCHAR),
                   new SqlOutParameter("p_certification_id", Types.INTEGER) // Return the ID of the linked/created cert
               );

           MapSqlParameterSource in = new MapSqlParameterSource()
               .addValue("p_student_id", studentId)
               .addValue("p_cert_name", certInfo.getName()) // Use certInfo object
               .addValue("p_issuing_org", certInfo.getIssuingOrganization()) // Use certInfo object
               .addValue("p_cert_date", certDate)
               .addValue("p_expiry_date", expiryDate) // Pass null if not provided
               .addValue("p_credential_id", credentialId); // Pass the (potentially generated) credentialId

           Map<String, Object> out = call.execute(in);
           Integer certificationId = (Integer) out.get("p_certification_id");

           if (certificationId != null) {
               // Return a representation of the linked certification
               // We might need to fetch the full details if the SP doesn't return them
               Certification linkedCert = new Certification();
               linkedCert.setCertificationId(certificationId);
               linkedCert.setName(certInfo.getName()); // Use name from input
               linkedCert.setIssuingOrganization(certInfo.getIssuingOrganization()); // Use org from input
               // Set other fields if returned by SP or fetched separately
               return linkedCert;
           } else {
               throw new RuntimeException("Failed to add certification '" + certInfo.getName() + "' for student " + studentId);
           }

       } catch (Exception e) {
           System.err.println("sp_add_student_certification failed: " + e.getMessage());
           throw new RuntimeException("Error adding certification: " + e.getMessage(), e);
       }
  }

  @Override
  public void removeCertificationFromStudent(Integer studentId, Integer certificationId) {
       try {
           SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
               .withProcedureName("sp_remove_student_certification") // Assumed SP name
               .declareParameters(
                   new SqlParameter("p_student_id", Types.INTEGER),
                   new SqlParameter("p_certification_id", Types.INTEGER),
                   new SqlOutParameter("p_rows_deleted", Types.INTEGER)
               );

           MapSqlParameterSource in = new MapSqlParameterSource()
               .addValue("p_student_id", studentId)
               .addValue("p_certification_id", certificationId);

           Map<String, Object> out = call.execute(in);
           Integer rows = (Integer) out.get("p_rows_deleted");
            if (rows == null || rows == 0) {
               System.err.println("Warning: No student_certification link found to delete for student " + studentId + ", certification " + certificationId);
               // Optionally throw an exception
               // throw new RuntimeException("Certification link not found for deletion.");
           }
       } catch (Exception e) {
           System.err.println("sp_remove_student_certification failed: " + e.getMessage());
           throw new RuntimeException("Error removing certification link: " + e.getMessage(), e);
       }
  }

}
