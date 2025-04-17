package com.campus.repository;

import com.campus.model.Company;
import com.campus.model.Recruiter;
import com.campus.repository.CompanyRepository;
import com.campus.repository.RecruiterRepository;
import com.campus.repository.UserRepository;

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
public class RecruiterRepositoryImpl implements RecruiterRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private RowMapper<Recruiter> recruiterRowMapper = (rs, rowNum) -> {
    Recruiter recruiter = new Recruiter();
    recruiter.setRecruiterId(rs.getInt("recruiterID"));

    Company company = new Company();
    company.setCompanyId(rs.getInt("companyID"));
    recruiter.setCompany(company);

    recruiter.setPosition(rs.getString("position"));

    // Load the user and company
    userRepository.findById(recruiter.getRecruiterId()).ifPresent(recruiter::setUser);
    companyRepository.findById(company.getCompanyId()).ifPresent(recruiter::setCompany);

    return recruiter;
  };

  @Override
  public Recruiter save(Recruiter recruiter) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_recruiter")
        .declareParameters(
            new SqlParameter   ("p_recruiter_id",  Types.INTEGER),
            new SqlParameter   ("p_company_id",    Types.INTEGER),
            new SqlParameter("p_position",      Types.VARCHAR),
            new SqlOutParameter("p_rows_inserted", Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_recruiter_id", recruiter.getRecruiterId())
        .addValue("p_company_id",   recruiter.getCompany().getCompanyId())
        .addValue("p_position",     recruiter.getPosition());

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_inserted");
    if (rows == null || rows != 1) {
      System.err.println("Warning: expected 1 recruiter inserted, got " + rows);
    }

    return recruiter;
  }


  @Override
  public Recruiter update(Recruiter recruiter) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_recruiter")
        .declareParameters(
            new SqlParameter   ("p_recruiter_id", Types.INTEGER),
            new SqlParameter   ("p_company_id",   Types.INTEGER),
            new SqlParameter   ("p_position",     Types.VARCHAR),
            new SqlOutParameter("p_rows_updated", Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_recruiter_id", recruiter.getRecruiterId())
        .addValue("p_company_id",   recruiter.getCompany().getCompanyId())
        .addValue("p_position",     recruiter.getPosition());

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no recruiter row updated for ID="
          + recruiter.getRecruiterId());
    }

    return recruiter;
  }


  @Override
  public boolean deleteById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_recruiter_by_id")
          .declareParameters(
              new SqlParameter   ("p_recruiter_id", Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_recruiter_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      return rows != null && rows > 0;

    } catch (Exception e) {
      System.err.println("sp_delete_recruiter_by_id failed: " + e.getMessage());
      return false;
    }
  }


  @Override
  public Optional<Recruiter> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_recruiter_by_id")
          .returningResultSet("rs", recruiterRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_recruiter_id", id);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Recruiter> list = (List<Recruiter>) out.get("rs");

      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("sp_get_recruiter_by_id failed: " + e.getMessage());
      return Optional.empty();
    }
  }


  @Override
  public List<Recruiter> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_recruiters")
          .returningResultSet("rs", recruiterRowMapper);

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Recruiter> list = (List<Recruiter>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: use proper logging
      System.err.println("sp_get_all_recruiters failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Recruiter> findByCompanyId(Integer companyId) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_recruiters_by_company_id")
          .returningResultSet("rs", recruiterRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_company_id", companyId);

      Map<String,Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Recruiter> recruiters = (List<Recruiter>) out.get("rs");
      return recruiters;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_recruiters_by_company_id failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public List<Recruiter> findByPosition(String position) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_recruiters_by_position")
          .returningResultSet("rs", recruiterRowMapper);

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_position", position);

      Map<String, Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Recruiter> recruiters = (List<Recruiter>) out.get("rs");
      return recruiters;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_recruiters_by_position failed: " + e.getMessage());
      return List.of();
    }
  }

}