package com.campus.repository;

import com.campus.model.College;
import com.campus.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    String sql = "INSERT INTO student (studentID, collegeID, major, gpa, resume, isPlaced, totalApplicationsCount) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.update(sql,
        student.getStudentId(),
        student.getCollege().getCollegeId(),
        student.getMajor(),
        student.getGpa(),
        student.getResume(),
        student.getPlaced(),
        student.getTotalApplicationsCount()
    );

    return student;
  }

  @Override
  public Student update(Student student) {
    String sql = "UPDATE student SET collegeID = ?, major = ?, gpa = ?, resume = ?, isPlaced = ?, " +
        "totalApplicationsCount = ? WHERE studentID = ?";

    jdbcTemplate.update(sql,
        student.getCollege().getCollegeId(),
        student.getMajor(),
        student.getGpa(),
        student.getResume(),
        student.getPlaced(),
        student.getTotalApplicationsCount(),
        student.getStudentId()
    );

    return student;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM student WHERE studentID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<Student> findById(Integer id) {
    String sql = "SELECT * FROM student WHERE studentID = ?";

    try {
      Student student = jdbcTemplate.queryForObject(sql, studentRowMapper, id);
      return Optional.ofNullable(student);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Student> findAll() {
    String sql = "SELECT * FROM student";
    return jdbcTemplate.query(sql, studentRowMapper);
  }

  @Override
  public List<Student> findByCollegeId(Integer collegeId) {
    String sql = "SELECT * FROM student WHERE collegeID = ?";
    return jdbcTemplate.query(sql, studentRowMapper, collegeId);
  }

  @Override
  public List<Student> findPlacedStudents() {
    String sql = "SELECT * FROM student WHERE isPlaced = TRUE";
    return jdbcTemplate.query(sql, studentRowMapper);
  }

  @Override
  public List<Student> findByMajor(String major) {
    String sql = "SELECT * FROM student WHERE major = ?";
    return jdbcTemplate.query(sql, studentRowMapper, major);
  }

  @Override
  public List<Student> findByGpaGreaterThanEqual(double gpa) {
    String sql = "SELECT * FROM student WHERE gpa >= ?";
    return jdbcTemplate.query(sql, studentRowMapper, gpa);
  }
}