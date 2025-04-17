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
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    // prepare values (default postDate to today if null)
    int    ivId    = experience.getInterview().getInterviewId();
    int    stuId   = experience.getStudent().getStudentId();
    String comment = experience.getComment();
    Integer rating = experience.getRating();   // assuming rating is Integer
    java.sql.Date postDate = experience.getPostDate() != null
        ? java.sql.Date.valueOf(experience.getPostDate())
        : java.sql.Date.valueOf(LocalDate.now());

    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_interview_experience")
        .declareParameters(
            new SqlParameter   ("p_interview_id", Types.INTEGER),
            new SqlParameter   ("p_student_id",   Types.INTEGER),
            new SqlParameter   ("p_comment",      Types.LONGVARCHAR),
            new SqlParameter("p_rating",       Types.INTEGER),
            new SqlParameter   ("p_post_date",    Types.DATE),
            new SqlOutParameter("p_new_id",       Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_interview_id", ivId)
        .addValue("p_student_id",   stuId)
        .addValue("p_comment",      comment)
        .addValue("p_rating",       rating)
        .addValue("p_post_date",    postDate);

    Map<String,Object> out = call.execute(in);
    Integer newId = (Integer) out.get("p_new_id");
    if (newId != null) {
      experience.setExperienceId(newId);
    } else {
      System.err.println("Warning: failed to retrieve new experience ID");
    }

    return experience;
  }


  @Override
  public InterviewExperience update(InterviewExperience experience) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_interview_experience")
        .declareParameters(
            new SqlParameter   ("p_experience_id", Types.INTEGER),
            new SqlParameter   ("p_interview_id",  Types.INTEGER),
            new SqlParameter   ("p_student_id",    Types.INTEGER),
            new SqlParameter   ("p_comment",       Types.LONGVARCHAR),
            new SqlParameter   ("p_rating",        Types.INTEGER),
            new SqlParameter   ("p_post_date",     Types.DATE),
            new SqlOutParameter("p_rows_updated",  Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_experience_id", experience.getExperienceId())
        .addValue("p_interview_id",  experience.getInterview().getInterviewId())
        .addValue("p_student_id",    experience.getStudent().getStudentId())
        .addValue("p_comment",       experience.getComment())
        .addValue("p_rating",        experience.getRating())
        .addValue("p_post_date",     Date.valueOf(experience.getPostDate()));

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no interview experience row updated for ID="
          + experience.getExperienceId());
    }

    return experience;
  }


  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_interview_experience_by_id")
          .declareParameters(
              new SqlParameter   ("p_experience_id", Types.INTEGER),
              new SqlOutParameter("p_rows_deleted",  Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_experience_id", id);

      Map<String,Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_delete_interview_experience_by_id failed: " + e.getMessage());
      return false;
    }
  }

  @Override
  public Optional<InterviewExperience> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interview_experience_by_id")
          .returningResultSet("rs", experienceRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_experience_id", id);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<InterviewExperience> list =
          (List<InterviewExperience>) out.get("rs");

      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println(
          "sp_get_interview_experience_by_id failed: " + e.getMessage()
      );
      return Optional.empty();
    }
  }


  @Override
  public List<InterviewExperience> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_interview_experiences")
          .returningResultSet("rs", experienceRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<InterviewExperience> list =
          (List<InterviewExperience>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_interview_experiences failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<InterviewExperience> findByInterviewId(Integer interviewId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interview_experiences_by_interview_id")
          .returningResultSet("rs", experienceRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_interview_id", interviewId);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<InterviewExperience> list =
          (List<InterviewExperience>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_interview_experiences_by_interview_id failed: "
          + e.getMessage());
      return List.of();
    }
  }

  @Override
  public List<InterviewExperience> findByStudentId(Integer studentId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_interview_experiences_by_student_id")
          .returningResultSet("rs", experienceRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_student_id", studentId);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<InterviewExperience> list =
          (List<InterviewExperience>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_interview_experiences_by_student_id failed: "
          + e.getMessage());
      return List.of();
    }
  }

}