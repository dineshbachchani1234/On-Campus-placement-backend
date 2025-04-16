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
    String sql = "INSERT INTO admin (adminID) VALUES (?)";

    jdbcTemplate.update(sql, admin.getAdminId());

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
    String sql = "DELETE FROM admin WHERE adminID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<Admin> findById(Integer id) {
    String sql = "SELECT * FROM admin WHERE adminID = ?";

    try {
      Admin admin = jdbcTemplate.queryForObject(sql, adminRowMapper, id);
      return Optional.ofNullable(admin);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Admin> findAll() {
    String sql = "SELECT * FROM admin";
    return jdbcTemplate.query(sql, adminRowMapper);
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