package com.campus.repository;

import com.campus.model.PlacementReport;
import com.campus.model.PlacementReportId;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlacementReportRepositoryImpl implements PlacementReportRepository{

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private static final RowMapper<PlacementReport> ROW_MAPPER = new RowMapper<>() {
    @Override
    public PlacementReport mapRow(ResultSet rs, int rowNum) throws SQLException {
      PlacementReport pr = new PlacementReport();
      PlacementReportId id = new PlacementReportId();
      id.setCollegeId(rs.getInt("collegeID"));
      id.setYear(rs.getInt("year"));
      pr.setId(id);
      pr.setPlacedStudents(rs.getInt("placedStudents"));
      pr.setTotalStudents(rs.getInt("totalStudents"));
      pr.setReportDate(rs.getDate("reportDate").toLocalDate());
      return pr;
    }
  };


  @Override
  public List<PlacementReport> findAll() {
    String sql = "SELECT collegeID, year, placedStudents, totalStudents, reportDate FROM placementreport";
    return jdbcTemplate.query(sql, ROW_MAPPER);
  }

  @Override
  public List<PlacementReport> findByYear(int year) {
    String sql = "SELECT collegeID, year, placedStudents, totalStudents, reportDate FROM placement_report";
    return jdbcTemplate.query(sql, ROW_MAPPER);
  }

  @Override
  public List<PlacementReport> findByCollegeId(int collegeID) {
    String sql = "SELECT collegeID, year, placedStudents, totalStudents, reportDate "
        + "FROM placement_report WHERE collegeID = ?";
    return jdbcTemplate.query(sql, ROW_MAPPER, collegeID);
  }
}
