package com.campus.controller;

import com.campus.model.Recruiter;
import com.campus.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiters")
public class RecruiterController {

  @Autowired
  private RecruiterService recruiterService;

  @PostMapping
  public String createRecruiter(@RequestBody Recruiter recruiter) {
    recruiterService.createRecruiter(recruiter);
    return "Recruiter created successfully";
  }

  @GetMapping("/{id}")
  public Recruiter getRecruiterById(@PathVariable int id) {
    return recruiterService.getRecruiterById(id);
  }

  @GetMapping
  public List<Recruiter> getAllRecruiters() {
    return recruiterService.getAllRecruiters();
  }

  @PutMapping("/{id}")
  public String updateRecruiter(@PathVariable int id, @RequestBody Recruiter recruiter) {
    recruiter.setRecruiterId(id);
    recruiterService.updateRecruiter(recruiter);
    return "Recruiter updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteRecruiter(@PathVariable int id) {
    recruiterService.deleteRecruiter(id);
    return "Recruiter deleted successfully";
  }
}