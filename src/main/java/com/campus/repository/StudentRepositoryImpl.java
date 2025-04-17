package com.campus.repository;

import com.campus.model.College;
import com.campus.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

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

    // Load the user
    userRepository.findById(student.getStudentId()).ifPresent(student::setUser);

    return student;
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

}