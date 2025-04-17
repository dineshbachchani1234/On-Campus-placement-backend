package com.campus.repository;

import com.campus.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class JobRepositoryImpl implements JobRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Job job) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_insert_joblisting")
        .declareParameters(
            new SqlParameter   ("p_title",       Types.VARCHAR),
            new SqlParameter   ("p_description", Types.LONGVARCHAR),
            new SqlParameter   ("p_salary",      Types.DECIMAL),
            new SqlParameter("p_job_type",    Types.VARCHAR),
            new SqlOutParameter("p_new_id",      Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_title",       job.getTitle())
        .addValue("p_description", job.getDescription())
        .addValue("p_salary",      job.getSalary())
        .addValue("p_job_type",    job.getJobType().toString());

    Map<String,Object> out = call.execute(in);
    Integer newId = (Integer) out.get("p_new_id");
    if (newId != null) {
      job.setJobId(newId);
    }
  }


  @Override
  public Job findById(int id) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_get_joblisting_by_id")
        .returningResultSet("rs", new JobRowMapper());

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_job_id", id);

    Map<String, Object> out = call.execute(in);
    @SuppressWarnings("unchecked")
    List<Job> list = (List<Job>) out.get("rs");

    if (list.isEmpty()) {
      throw new EmptyResultDataAccessException(
          "No job listing found for jobID=" + id, 1);
    }
    return list.get(0);
  }

  @Override
  public List<Job> findAll() {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_get_all_joblistings")
          .returningResultSet("rs", new JobRowMapper());

      Map<String, Object> out = call.execute();
      @SuppressWarnings("unchecked")
      List<Job> jobs = (List<Job>) out.get("rs");
      return jobs;

    } catch (Exception e) {
      // TODO: use proper logging
      System.err.println("sp_get_all_joblistings failed: " + e.getMessage());
      return List.of();
    }
  }


  @Override
  public void update(Job job) {
    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("sp_update_joblisting")
        .declareParameters(
            new SqlParameter   ("p_job_id",       Types.INTEGER),
            new SqlParameter   ("p_title",        Types.VARCHAR),
            new SqlParameter   ("p_description",  Types.LONGVARCHAR),
            new SqlParameter   ("p_salary",       Types.DECIMAL),
            new SqlParameter   ("p_job_type",     Types.VARCHAR),
            new SqlOutParameter("p_rows_updated", Types.INTEGER)
        );

    MapSqlParameterSource in = new MapSqlParameterSource()
        .addValue("p_job_id",       job.getJobId())
        .addValue("p_title",        job.getTitle())
        .addValue("p_description",  job.getDescription())
        .addValue("p_salary",       job.getSalary())
        .addValue("p_job_type",     job.getJobType().toString());

    Map<String,Object> out = call.execute(in);
    Integer rows = (Integer) out.get("p_rows_updated");
    if (rows == null || rows == 0) {
      System.err.println("Warning: no joblisting row updated for jobID=" + job.getJobId());
    }
  }

  @Override
  public void deleteById(int id) {
    try {
      SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("sp_delete_joblisting_by_id")
          .declareParameters(
              new SqlParameter   ("p_job_id",        Types.INTEGER),
              new SqlOutParameter("p_rows_deleted",  Types.INTEGER)
          );

      MapSqlParameterSource in = new MapSqlParameterSource()
          .addValue("p_job_id", id);

      Map<String, Object> out = call.execute(in);
      Integer rows = (Integer) out.get("p_rows_deleted");
      if (rows == null || rows == 0) {
        System.err.println("Warning: no joblisting deleted for jobID=" + id);
      }
    } catch (Exception e) {
      System.err.println("sp_delete_joblisting_by_id failed: " + e.getMessage());
    }
  }


  private static class JobRowMapper implements RowMapper<Job> {
    @Override
    public Job mapRow(ResultSet rs, int rowNum) throws SQLException {
      Job job = new Job();
      job.setJobId(rs.getInt("jobID"));
      job.setTitle(rs.getString("title"));
      job.setDescription(rs.getString("description"));
      job.setSalary(rs.getDouble("salary"));
      job.setJobType(rs.getString("job_type"));
      return job;
    }
  }
}