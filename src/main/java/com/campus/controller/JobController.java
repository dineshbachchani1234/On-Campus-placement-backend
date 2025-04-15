package com.campus.controller;

import com.campus.model.Job;
import com.campus.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

  @Autowired
  private JobService jobService;

  @PostMapping
  public String createJob(@RequestBody Job job) {
    jobService.createJob(job);
    return "Job created successfully";
  }

  @GetMapping("/{id}")
  public Job getJobById(@PathVariable int id) {
    return jobService.getJobById(id);
  }

  @GetMapping
  public List<Job> getAllJobs() {
    return jobService.getAllJobs();
  }

  @PutMapping("/{id}")
  public String updateJob(@PathVariable int id, @RequestBody Job job) {
    job.setJobId(id);
    jobService.updateJob(job);
    return "Job updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteJob(@PathVariable int id) {
    jobService.deleteJob(id);
    return "Job deleted successfully";
  }
}
