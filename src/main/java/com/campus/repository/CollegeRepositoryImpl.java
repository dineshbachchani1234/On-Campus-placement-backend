package com.campus.repository;

import com.campus.model.College;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class CollegeRepositoryImpl implements CollegeRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private RowMapper<College> collegeRowMapper = (rs, rowNum) -> {
    College college = new College();
    college.setCollegeId(rs.getInt("collegeID"));
    college.setName(rs.getString("name"));
    college.setNumberOfStudents(rs.getInt("numberOfStudents"));
    return college;
  };

  @Override
  public College save(College college) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_college")
        .declareParameters(
            new SqlParameter("p_name",               Types.VARCHAR),
            new SqlParameter   ("p_number_of_students", Types.INTEGER),
            new SqlOutParameter("p_new_id",             Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_name",               college.getName())
        .addValue("p_number_of_students",
            college.getNumberOfStudents() != null
                ? college.getNumberOfStudents()
                : 0);

    Map<String,Object> out = call.execute(in);
    Integer newId = (Integer) out.get("p_new_id");
    college.setCollegeId(newId);

    return college;
  }


  @Override
  public College update(College college) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_college")
        .declareParameters(
            new SqlParameter   ("p_college_id",           Types.INTEGER),
            new SqlParameter   ("p_name",                 Types.VARCHAR),
            new SqlParameter   ("p_number_of_students",   Types.INTEGER),
            new SqlOutParameter("p_rows_updated",         Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_college_id",         college.getCollegeId())
        .addValue("p_name",               college.getName())
        .addValue("p_number_of_students", college.getNumberOfStudents());

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no college row updated for ID="
          + college.getCollegeId());
    }

    return college;
  }


  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_college_by_id")
          .declareParameters(
              new SqlParameter   ("p_college_id",   Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_college_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_delete_college_by_id failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public Optional<College> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_college_by_id")
          .returningResultSet("rs", collegeRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_college_id", id);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<College> list = (List<College>) out.get("rs");

      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("sp_get_college_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public List<College> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_colleges")
          .returningResultSet("rs", collegeRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<College> list = (List<College>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_colleges failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public Optional<College> findByName(String name) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_college_by_name")
          .returningResultSet("rs", collegeRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_name", name);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<College> list = (List<College>) out.get("rs");

      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (DataAccessException e) {
      // No row found or other error
      return Optional.empty();
    }
  }


  @Override
  public boolean existsByName(String name) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_exists_college_by_name")
          .declareParameters(
              new SqlParameter   ("p_name",  Types.VARCHAR),
              new SqlOutParameter("p_count", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_name", name);

      Map<String, Object> out = call.execute(in);
      Integer count = (Integer) out.get("p_count");
      return count != null && count > 0;

    } catch (Exception e) {
      System.err.println("sp_exists_college_by_name failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public boolean updateNumberOfStudents(Integer collegeId, Integer numberOfStudents) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_update_college_number_of_students")
          .declareParameters(
              new SqlParameter   ("p_college_id",         Types.INTEGER),
              new SqlParameter   ("p_number_of_students", Types.INTEGER),
              new SqlOutParameter("p_rows_updated",       Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_college_id",         collegeId)
          .addValue("p_number_of_students", numberOfStudents);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_updated");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_update_college_number_of_students failed: " + e.getMessage());
      return false;
    }
  }

}