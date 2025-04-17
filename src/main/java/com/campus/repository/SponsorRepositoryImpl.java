package com.campus.repository;

import com.campus.model.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource; // Added import
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Added import
import java.util.stream.Collectors; // Added import

@Repository
public class SponsorRepositoryImpl implements SponsorRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public Sponsor save(Sponsor sponsor) { // Corrected return type
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_sponsor")
        .declareParameters(
            new SqlParameter("p_name",             Types.VARCHAR),
            new SqlParameter   ("p_contribution_amt", Types.DECIMAL),
            new SqlParameter   ("p_email",            Types.VARCHAR),
            new SqlOutParameter("p_rows_inserted",    Types.INTEGER) // Assuming SP doesn't return ID yet
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_name",             sponsor.getName())
        .addValue("p_contribution_amt", sponsor.getAmount())
        .addValue("p_email",            sponsor.getEmail());

    // Execute and check rows inserted, but return the input object as per interface
    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_inserted");
     if (rows == null || rows != 1) {
       System.err.println("Warning: expected 1 sponsor inserted, got " + rows);
     }
     // If SP were updated to return ID:
     // Integer newId = (Integer) out.get("p_new_id");
     // if (newId != null) {
     //     sponsor.setSponsorId(newId);
     // }
    return sponsor;
  }

  @Override
  public Optional<Sponsor> findById(Integer id) { // Corrected signature
    try {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("sp_get_sponsor_by_id")
            .declareParameters(new SqlParameter("p_sponsor_id", Types.INTEGER))
            .returningResultSet("rs", new SponsorRowMapper());

        MapSqlParameterSource in = new MapSqlParameterSource().addValue("p_sponsor_id", id);
        Map<String, Object> out = call.execute(in);

        @SuppressWarnings("unchecked")
        List<Sponsor> list = (List<Sponsor>) out.get("rs");
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    } catch (Exception e) {
        System.err.println("Error calling sp_get_sponsor_by_id: " + e.getMessage());
        return Optional.empty();
    }
  }


  @Override
  public List<Sponsor> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_sponsors")
          .returningResultSet("rs", new SponsorRowMapper());

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Sponsor> list = (List<Sponsor>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_sponsors failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public boolean update(Sponsor sponsor) { // Corrected return type
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_sponsor")
        .declareParameters(
            new SqlParameter   ("p_sponsor_id",       Types.INTEGER),
            new SqlParameter   ("p_name",             Types.VARCHAR),
            new SqlParameter   ("p_contribution_amt", Types.DECIMAL),
            new SqlParameter   ("p_email",            Types.VARCHAR),
            new SqlOutParameter("p_rows_updated",     Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_sponsor_id",       sponsor.getSponsorId())
        .addValue("p_name",             sponsor.getName())
        .addValue("p_contribution_amt", sponsor.getAmount())
        .addValue("p_email",            sponsor.getEmail());

    Map<String, Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no sponsor row updated for ID="
          + sponsor.getSponsorId());
    }
    // Return true if rows were updated
    return rows != null && rows > 0;
  }


  @Override
  public boolean deleteById(Integer id) { // Corrected signature and return type
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_sponsor_by_id")
          .declareParameters(
              new SqlParameter   ("p_sponsor_id",   Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_sponsor_id", id);

      Map<String,Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      if (rows == null || rows == 0) {
        System.err.println("Warning: no sponsor deleted for ID=" + id);
        return false; // Indicate deletion failed or no rows affected
      }
      return true; // Indicate success
    } catch (Exception e) {
      System.err.println("sp_delete_sponsor_by_id failed: " + e.getMessage());
      return false; // Indicate failure due to exception
    }
  }

   @Override
    public Optional<Sponsor> findByEmail(String email) {
        // Implementation needed if required - currently returns empty
        // Need a stored procedure sp_get_sponsor_by_email
        return Optional.empty();
    }

    @Override
    public Optional<Sponsor> findByName(String name) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_sponsor_by_name") // Assuming this SP exists
                .declareParameters(new SqlParameter("p_name", Types.VARCHAR))
                .returningResultSet("rs", new SponsorRowMapper());

            MapSqlParameterSource in = new MapSqlParameterSource().addValue("p_name", name);
            Map<String, Object> out = call.execute(in);

            @SuppressWarnings("unchecked")
            List<Sponsor> list = (List<Sponsor>) out.get("rs");
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            System.err.println("Error calling sp_get_sponsor_by_name for name '" + name + "': " + e.getMessage());
            return Optional.empty();
        }
    }

   @Override
    public List<Sponsor> findByIdIn(List<Integer> sponsorIds) {
        if (sponsorIds == null || sponsorIds.isEmpty()) {
            return List.of();
        }

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_sponsors_by_ids")
                .declareParameters(new SqlParameter("p_sponsor_ids", Types.VARCHAR))
                .returningResultSet("sponsors", new SponsorRowMapper());

        String sponsorIdString = sponsorIds.stream()
                                           .map(String::valueOf)
                                           .collect(Collectors.joining(","));

        SqlParameterSource in = new MapSqlParameterSource().addValue("p_sponsor_ids", sponsorIdString);

        try {
            Map<String, Object> out = jdbcCall.execute(in);
            @SuppressWarnings("unchecked")
            List<Sponsor> sponsors = (List<Sponsor>) out.get("sponsors");
            return sponsors != null ? sponsors : List.of();
        } catch (Exception e) {
            System.err.println("Error calling sp_get_sponsors_by_ids: " + e.getMessage());
            return List.of();
        }
    }


  private static class SponsorRowMapper implements RowMapper<Sponsor> {
    @Override
    public Sponsor mapRow(ResultSet rs, int rowNum) throws SQLException {
      Sponsor sponsor = new Sponsor();
      sponsor.setSponsorId(rs.getInt("sponsorID")); // Corrected column name based on SP
      sponsor.setName(rs.getString("name"));
      sponsor.setAmount(rs.getBigDecimal("amount")); // Corrected column name based on SP
      sponsor.setEmail(rs.getString("email"));
      return sponsor;
    }
  }
}
