package com.campus.controller;

import com.campus.model.Interview;
import com.campus.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

  @Autowired
  private InterviewService interviewService;

  @PostMapping
  public String createInterview(@RequestBody Interview interview) {
    interviewService.createInterview(interview);
    return "Interview created successfully";
  }

  @GetMapping("/{id}")
  public Interview getInterviewById(@PathVariable int id) {
    return interviewService.getInterviewById(id);
  }

  @GetMapping
  public List<Interview> getAllInterviews() {
    return interviewService.getAllInterviews();
  }

  @PutMapping("/{id}")
  public String updateInterview(@PathVariable int id, @RequestBody Interview interview) {
    interview.setInterviewId(id);
    interviewService.updateInterview(interview);
    return "Interview updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteInterview(@PathVariable int id) {
    interviewService.deleteInterview(id);
    return "Interview deleted successfully";
  }
}
