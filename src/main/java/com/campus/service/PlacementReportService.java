package com.campus.service;

import com.campus.model.PlacementReport;
import java.util.List;

public interface PlacementReportService {

  List<PlacementReport> getAllReports();
  List<PlacementReport> getReportsByYear(int year);
  List<PlacementReport> getReportsByCollege(int collegeID);

}
