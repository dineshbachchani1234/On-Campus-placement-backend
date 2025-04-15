package com.campus.repository;

import com.campus.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StudentRepositoryImpl implements StudentRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Student student) {
    String sql = "INSERT INTO student (firstName, lastName, email, university, gpa) VALUES (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, student.getFirstName(), student.getLastName(), student.getEmail(), student.getUniversity(), student.getGpa());
  }

  @Override
  public Student findById(int id) {
    String sql = "SELECT * FROM student WHERE studentID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new StudentRowMapper());
  }

  @Override
  public List<Student> findAll() {
    String sql = "SELECT * FROM student";
    return jdbcTemplate.query(sql, new StudentRowMapper());
  }

  @Override
  public void update(Student student) {
    String sql = "UPDATE student SET firstName = ?, lastName = ?, email = ?, university = ?, gpa = ? WHERE studentID = ?";
    jdbcTemplate.update(sql, student.getFirstName(), student.getLastName(), student.getEmail(), student.getUniversity(), student.getGpa(), student.getStudentId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM student WHERE studentID = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class StudentRowMapper implements RowMapper<Student> {
    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
      Student student = new Student();
      student.setStudentId(rs.getInt("studentID"));
      student.setFirstName(rs.getString("firstName"));
      student.setLastName(rs.getString("lastName"));
      student.setEmail(rs.getString("email"));
      student.setUniversity(rs.getString("university"));
      student.setGpa(rs.getDouble("gpa"));
      return student;
    }
  }
}
