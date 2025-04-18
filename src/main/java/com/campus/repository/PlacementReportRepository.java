package com.campus.repository;

import com.campus.model.PlacementReport;
import java.util.List;

public interface PlacementReportRepository {

  List<PlacementReport> findAll();
  List<PlacementReport> findByYear(int year);
  List<PlacementReport> findByCollegeId(int collegeID);

}
