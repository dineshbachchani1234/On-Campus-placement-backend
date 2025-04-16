package com.campus.repository;

import com.campus.model.Interview;
import com.campus.model.InterviewExperience;
import com.campus.model.Student;
import com.campus.repository.InterviewExperienceRepository;
import com.campus.repository.InterviewRepository;
import com.campus.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InterviewExperienceRepositoryImpl implements InterviewExperienceRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private InterviewRepository interviewRepository;

  @Autowired
  private StudentRepository studentRepository;

  private RowMapper<InterviewExperience> experienceRowMapper = (rs, rowNum) -> {
    InterviewExperience experience = new InterviewExperience();
    experience.setExperienceId(rs.getInt("experienceID"));

    Interview interview = new Interview();
    interview.setInterviewId(rs.getInt("interviewID"));
    experience.setInterview(interview);

    Student student = new Student();
    student.setStudentId(rs.getInt("studentID"));
    experience.setStudent(student);

    experience.setComment(rs.getString("comment"));
    experience.setRating(rs.getInt("rating"));
    experience.setPostDate(rs.getDate("postDate").toLocalDate());

    // Load the interview and student
    interviewRepository.findById(interview.getInterviewId()).ifPresent(experience::setInterview);
    studentRepository.findById(student.getStudentId()).ifPresent(experience::setStudent);

    return experience;
  };

  @Override
  public InterviewExperience save(InterviewExperience experience) {
    String sql = "INSERT INTO interviewexperience (interviewID, studentID, comment, rating, postDate) " +
        "VALUES (?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, experience.getInterview().getInterviewId());
      ps.setInt(2, experience.getStudent().getStudentId());
      ps.setString(3, experience.getComment());
      ps.setObject(4, experience.getRating());
      ps.setDate(5, experience.getPostDate() != null ?
          Date.valueOf(experience.getPostDate()) :
          Date.valueOf(LocalDate.now()));
      return ps;
    }, keyHolder);

    experience.setExperienceId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    return experience;
  }

  @Override
  public InterviewExperience update(InterviewExperience experience) {
    String sql = "UPDATE interviewexperience SET interviewID = ?, studentID = ?, comment = ?, " +
        "rating = ?, postDate = ? WHERE experienceID = ?";

    jdbcTemplate.update(sql,
        experience.getInterview().getInterviewId(),
        experience.getStudent().getStudentId(),
        experience.getComment(),
        experience.getRating(),
        Date.valueOf(experience.getPostDate()),
        experience.getExperienceId()
    );

    return experience;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM interviewexperience WHERE experienceID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<InterviewExperience> findById(Integer id) {
    String sql = "SELECT * FROM interviewexperience WHERE experienceID = ?";

    try {
      InterviewExperience experience = jdbcTemplate.queryForObject(sql, experienceRowMapper, id);
      return Optional.ofNullable(experience);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<InterviewExperience> findAll() {
    String sql = "SELECT * FROM interviewexperience";
    return jdbcTemplate.query(sql, experienceRowMapper);
  }

  @Override
  public List<InterviewExperience> findByInterviewId(Integer interviewId) {
    String sql = "SELECT * FROM interviewexperience WHERE interviewID = ?";
    return jdbcTemplate.query(sql, experienceRowMapper, interviewId);
  }

  @Override
  public List<InterviewExperience> findByStudentId(Integer studentId) {
    String sql = "SELECT * FROM interviewexperience WHERE studentID = ?";
    return jdbcTemplate.query(sql, experienceRowMapper, studentId);
  }
}