package com.campus.controller;

import com.campus.model.College;
import com.campus.model.PlacementReport;
import com.campus.service.CollegeService;
import com.campus.service.PlacementReportService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

  @Autowired
  private PlacementReportService placementReportService;

  @Autowired
  private CollegeService collegeService;


  @GetMapping("/placement-reports")
  public List<PlacementReport> listAll() {
    return placementReportService.getAllReports();
  }

  @GetMapping("/year/{year}")
  public List<PlacementReport> byYear(@PathVariable int year) {
    return placementReportService.getReportsByYear(year);
  }

  @GetMapping("/college/{id}")
  public List<PlacementReport> byCollege(@PathVariable("id") int collegeID) {
    return placementReportService.getReportsByCollege(collegeID);
  }

  @GetMapping
  public List<College> listAllColleges() {
    return collegeService.getAllColleges();
  }

  @GetMapping("/{id}")
  public College getOne(@PathVariable("id") int collegeID) {
    return collegeService.getCollegeById(collegeID)
        .orElseThrow(() -> new RuntimeException("Not found: " + collegeID));
  }

}
