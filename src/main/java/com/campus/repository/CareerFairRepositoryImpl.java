package com.campus.repository;

import com.campus.model.CareerFair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class CareerFairRepositoryImpl implements CareerFairRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(CareerFair fair) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_careerfair")
        .declareParameters(
            new SqlParameter("p_title",       Types.VARCHAR),
            new SqlParameter   ("p_description", Types.LONGVARCHAR),
            new SqlParameter   ("p_date",        Types.DATE),
            new SqlParameter   ("p_location",    Types.VARCHAR),
            new SqlOutParameter("p_new_id",      Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_title",       fair.getTitle())
        .addValue("p_description", fair.getDescription())
        .addValue("p_date",        Date.valueOf(fair.getDate()))
        .addValue("p_location",    fair.getLocation());

    Map<String,Object> out = call.execute(in);
    // if you later want the generated ID:
    Integer newId = (Integer) out.get("p_new_id");
    fair.setFairId(newId);
  }


  @Override
  public CareerFair findById(int id) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_get_careerfair_by_id")
        .returningResultSet("rs", new CareerFairRowMapper());

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_fair_id", id);

    Map<String, Object> out = call.execute(in);
    @SuppressWarnings("unchecked")
    List<CareerFair> list = (List<CareerFair>) out.get("rs");

    if (list.isEmpty()) {
      throw new EmptyResultDataAccessException(
          "No career fair found for fairID=" + id, 1);
    }
    return list.get(0);
  }


  @Override
  public List<CareerFair> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_careerfairs")
          .returningResultSet("rs", new CareerFairRowMapper());

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<CareerFair> list = (List<CareerFair>) out.get("rs");
      return list;

    } catch (Exception e) {
      // TODO: replace with proper logging
      System.err.println("sp_get_all_careerfairs failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public void update(CareerFair fair) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_careerfair")
        .declareParameters(
            new SqlParameter   ("p_fair_id",      Types.INTEGER),
            new SqlParameter   ("p_title",        Types.VARCHAR),
            new SqlParameter   ("p_description",  Types.LONGVARCHAR),
            new SqlParameter   ("p_date",         Types.DATE),
            new SqlParameter   ("p_location",     Types.VARCHAR),
            new SqlOutParameter("p_rows_updated", Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_fair_id", fair.getFairId())
        .addValue("p_title",   fair.getTitle())
        .addValue("p_description", fair.getDescription())
        .addValue("p_date",    Date.valueOf(fair.getDate()))
        .addValue("p_location", fair.getLocation());

    Map<String, Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no career fair row updated for fairID=" + fair.getFairId());
    }
  }


  @Override
  public void deleteById(int id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_careerfair_by_id")
          .declareParameters(
              new SqlParameter   ("p_fair_id",      Types.INTEGER),
              new SqlOutParameter("p_rows_deleted", Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_fair_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      if (rows == null || rows == 0) {
        System.err.println("Warning: no career fair deleted for fairID=" + id);
      }
    } catch (Exception e) {
      System.err.println("sp_delete_careerfair_by_id failed: " + e.getMessage());
    }
  }



  private static class CareerFairRowMapper implements RowMapper<CareerFair> {
    @Override
    public CareerFair mapRow(ResultSet rs, int rowNum) throws SQLException {
      CareerFair fair = new CareerFair();
      fair.setFairId(rs.getInt("eventId"));
      fair.setTitle(rs.getString("title"));
      fair.setDescription(rs.getString("description"));
      fair.setDate(rs.getString("date"));
      fair.setLocation(rs.getString("location"));
      return fair;
    }
  }
}
