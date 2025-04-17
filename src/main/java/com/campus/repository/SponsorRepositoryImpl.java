package com.campus.repository;

import com.campus.model.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Repository
public class SponsorRepositoryImpl implements SponsorRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Sponsor sponsor) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_sponsor")
        .declareParameters(
            new SqlParameter("p_name",             Types.VARCHAR),
            new SqlParameter   ("p_contribution_amt", Types.DECIMAL),
            new SqlParameter   ("p_email",            Types.VARCHAR),
            new SqlOutParameter("p_rows_inserted",    Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_name",             sponsor.getName())
        .addValue("p_contribution_amt", sponsor.getAmount())
        .addValue("p_email",            sponsor.getEmail());

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_inserted");
    if (rows == null || rows != 1) {
      System.err.println("Warning: expected 1 sponsor inserted, got " + rows);
    }
  }


  @Override
  public Sponsor findById(int id) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_get_sponsor_by_id")
        .returningResultSet("rs", new SponsorRowMapper());

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_sponsor_id", id);

    Map<String, Object> out = call.execute(in);
    @SuppressWarnings("unchecked")
    List<Sponsor> list = (List<Sponsor>) out.get("rs");

    if (list.isEmpty()) {
      throw new EmptyResultDataAccessException("No sponsor found for id=" + id, 1);
    }
    return list.get(0);
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
  public void update(Sponsor sponsor) {
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
  }


  @Override
  public void deleteById(int id) {
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
      }
    } catch (Exception e) {
      System.err.println("sp_delete_sponsor_by_id failed: " + e.getMessage());
    }
  }


  private static class SponsorRowMapper implements RowMapper<Sponsor> {
    @Override
    public Sponsor mapRow(ResultSet rs, int rowNum) throws SQLException {
      Sponsor sponsor = new Sponsor();
      sponsor.setSponsorId(rs.getInt("sponsor_id"));
      sponsor.setName(rs.getString("name"));
      sponsor.setAmount(rs.getBigDecimal("contribution_amount"));
      sponsor.setEmail(rs.getString("email"));
      return sponsor;
    }
  }
}