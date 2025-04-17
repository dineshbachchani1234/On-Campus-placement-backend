package com.campus.repository;

import com.campus.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource; // Added import
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors; // Added import

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;



  @Override
  public Company findById(int id) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_get_company_by_id")
        .returningResultSet("rs", new CompanyRowMapper());

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_company_id", id);

    Map<String, Object> out = call.execute(in);
    @SuppressWarnings("unchecked")
    List<Company> list = (List<Company>) out.get("rs");

    if (list.isEmpty()) {
      throw new EmptyResultDataAccessException(
          "No company found for companyID=" + id, 1);
    }
    return list.get(0);
  }


  @Override
  public List<Company> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_companies")
          .returningResultSet("rs", new CompanyRowMapper());

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Company> list = (List<Company>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_companies failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public Company save(Company entity) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_company")
        .declareParameters(
            new SqlParameter("p_name",     Types.VARCHAR),
            new SqlParameter   ("p_industry", Types.VARCHAR),
            new SqlParameter   ("p_email",    Types.VARCHAR),
            new SqlOutParameter("p_new_id",   Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_name",     entity.getCompanyName())
        .addValue("p_industry", entity.getIndustry())
        .addValue("p_email",    entity.getCompanyEmail());

    Map<String,Object> out = call.execute(in);
    Integer newId = (Integer) out.get("p_new_id");
    if (newId != null) {
      entity.setCompanyId(newId);
    }
    return entity;
  }

  @Override
  public Company update(Company company) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_company")
        .declareParameters(
            new SqlParameter   ("p_company_id",   Types.INTEGER),
            new SqlParameter   ("p_name",         Types.VARCHAR),
            new SqlParameter   ("p_industry",     Types.VARCHAR),
            new SqlParameter   ("p_email",        Types.VARCHAR),
            new SqlOutParameter("p_rows_updated", Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_company_id", company.getCompanyId())
        .addValue("p_name",       company.getCompanyName())
        .addValue("p_industry",   company.getIndustry())
        .addValue("p_email",      company.getCompanyEmail());

    Map<String, Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no company row updated for ID="
          + company.getCompanyId());
    }

    return company;
  }


  @Override
  public boolean deleteById(Integer integer) {
    return false;
  }

  @Override
  public Optional<Company> findById(Integer id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_company_by_id")
          .returningResultSet("rs", new CompanyRowMapper());

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_company_id", id);

      Map<String,Object> out = call.execute(in);
      @SuppressWarnings("unchecked")
      List<Company> list = (List<Company>) out.get("rs");
      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(0));

    } catch (DataAccessException e) {
      // no row or other error
      return Optional.empty();
    }
  }


  @Override
  public void deleteById(int id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_company_by_id")
          .declareParameters(
              new SqlParameter   ("p_company_id", Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_company_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      if (rows == null || rows == 0) {
        System.err.println("Warning: no company deleted for ID=" + id);
      }
    } catch (Exception e) {
      System.err.println("sp_delete_company_by_id failed: " + e.getMessage());
    }
  }


  @Override
  public Optional<Company> findByName(String companyName) {
    try {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("sp_get_company_by_name") // Assuming this SP exists
            .declareParameters(new SqlParameter("p_name", Types.VARCHAR))
            .returningResultSet("rs", new CompanyRowMapper());

        MapSqlParameterSource in = new MapSqlParameterSource()
            .addValue("p_name", companyName);

        Map<String, Object> out = call.execute(in);
        @SuppressWarnings("unchecked")
        List<Company> list = (List<Company>) out.get("rs");

        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));

    } catch (Exception e) {
        // Log error properly
        System.err.println("Error calling sp_get_company_by_name for name '" + companyName + "': " + e.getMessage());
        return Optional.empty(); // Return empty on error
    }
  }

  @Override
  public List<Company> findByIndustry(String industry) {
    //return List.of();
    return null;
  }

  @Override
  public Optional<Company> findByEmail(String email) {
    return Optional.empty();
  }

  @Override
  public boolean existsByName(String companyName) {
    return false;
  }

  @Override
  public boolean existsByEmail(String email) {
     // TODO: Implement using a dedicated stored procedure or query if needed
    return findByEmail(email).isPresent(); // Basic implementation using findByEmail
  }

   @Override
   public boolean existsById(Integer id) {
       // Implement using findById
       return findById(id).isPresent();
   }

  @Override
  public List<Company> findByIdIn(List<Integer> companyIds) {
      if (companyIds == null || companyIds.isEmpty()) {
          return List.of(); // Return empty list if input is empty
      }

      SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
              .withProcedureName("sp_get_companies_by_ids")
              .declareParameters(new SqlParameter("p_company_ids", Types.VARCHAR)) // Input is comma-separated string
              .returningResultSet("companies", new CompanyRowMapper()); // Use existing mapper

      // Convert list of IDs to comma-separated string
      String companyIdString = companyIds.stream()
                                         .map(String::valueOf)
                                         .collect(Collectors.joining(","));

      SqlParameterSource in = new MapSqlParameterSource().addValue("p_company_ids", companyIdString);

      try {
          Map<String, Object> out = jdbcCall.execute(in);
          @SuppressWarnings("unchecked")
          List<Company> companies = (List<Company>) out.get("companies");
          return companies != null ? companies : List.of();
      } catch (Exception e) {
          System.err.println("Error calling sp_get_companies_by_ids: " + e.getMessage());
          // Log the error properly in a real application
          return List.of(); // Return empty list on error
      }
  }

  private static class CompanyRowMapper implements RowMapper<Company> {
    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
      Company company = new Company();
      company.setCompanyId(rs.getInt("companyID"));
      company.setCompanyName(rs.getString("companyName"));
      company.setIndustry(rs.getString("industry"));
      company.setCompanyEmail(rs.getString("companyEmail"));
      return company;
    }
  }
}
