package com.campus.repository;

import com.campus.model.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private UserRepository userRepository;

  private RowMapper<Admin> adminRowMapper = (rs, rowNum) -> {
    Admin admin = new Admin();
    admin.setAdminId(rs.getInt("adminID"));

    // Load the user
    userRepository.findById(admin.getAdminId()).ifPresent(admin::setUser);

    return admin;
  };

  @Override
  public Admin save(Admin admin) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_admin")
        .declareParameters(
            new SqlParameter   ("p_admin_id",      Types.INTEGER),
            new SqlOutParameter("p_rows_inserted", Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_admin_id", admin.getAdminId());

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_inserted");
    if (rows == null || rows != 1) {
      System.err.println("Warning: expected 1 admin inserted, got " + rows);
    }

    return admin;
  }


  @Override
  public Admin update(Admin admin) {
    // There are no fields to update in the admin table except the primary key
    // which shouldn't be updated
    return admin;
  }

  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_admin_by_id")
          .declareParameters(
              new SqlParameter   ("p_admin_id",     Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_admin_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_delete_admin_by_id failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public Optional<Admin> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_admin_by_id")
          .returningResultSet("rs", adminRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_admin_id", id);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Admin> list = (List<Admin>) out.get("rs");

      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("sp_get_admin_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public List<Admin> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_admins")
          .returningResultSet("rs", adminRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Admin> admins = (List<Admin>) out.get("rs");
      return admins;

    } catch (Exception e) {
      System.err.println("sp_get_all_admins failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public boolean generatePlacementReport(Integer collegeId, Integer year) {
    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("generate_placement_report");

    SqlParameterSource inParams = new MapSqlParameterSource()
        .addValue("college_id", collegeId)
        .addValue("report_year", year);

    try {
      jdbcCall.execute(inParams);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public double getPlacementRate(Integer collegeId, Integer year) {
    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withFunctionName("get_placement_rate")
        .declareParameters(
            new SqlParameter("college_id", Types.INTEGER),
            new SqlParameter("report_year", Types.INTEGER),
            new SqlOutParameter("return", Types.DECIMAL)
        );

    SqlParameterSource inParams = new MapSqlParameterSource()
        .addValue("college_id", collegeId)
        .addValue("report_year", year);

    Map<String, Object> result = jdbcCall.execute(inParams);
    Double rate = (Double) result.get("return");

    return rate != null ? rate : 0.0;
  }
}