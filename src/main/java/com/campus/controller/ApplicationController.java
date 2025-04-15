package com.campus.controller;

import com.campus.model.Application;
import com.campus.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

  @Autowired
  private ApplicationService applicationService;

  @PostMapping
  public String createApplication(@RequestBody Application app) {
    applicationService.createApplication(app);
    return "Application submitted successfully";
  }

  @GetMapping("/{id}")
  public Application getApplicationById(@PathVariable int id) {
    return applicationService.getApplicationById(id);
  }

  @GetMapping
  public List<Application> getAllApplications() {
    return applicationService.getAllApplications();
  }

  @PutMapping("/{id}")
  public String updateApplication(@PathVariable int id, @RequestBody Application app) {
    app.setApplicationId(id);
    applicationService.updateApplication(app);
    return "Application updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteApplication(@PathVariable int id) {
    applicationService.deleteApplication(id);
    return "Application deleted successfully";
  }
}
