package com.campus.service;

import com.campus.model.PlacementReport;
import com.campus.repository.JobListingRepository;
import com.campus.repository.PlacementReportRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlacementReportServiceImpl implements PlacementReportService{

  @Autowired
  private PlacementReportRepository placementReportRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public List<PlacementReport> getAllReports() {
    return placementReportRepository.findAll();
  }

  @Override
  public List<PlacementReport> getReportsByYear(int year) {
    return placementReportRepository.findByYear(year);
  }

  @Override
  public List<PlacementReport> getReportsByCollege(int collegeID) {
    return placementReportRepository.findByCollegeId(collegeID);
  }
}
