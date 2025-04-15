package com.campus.service;

import com.campus.model.Job;
import java.util.List;

public interface JobService {
  void createJob(Job job);
  Job getJobById(int id);
  List<Job> getAllJobs();
  void updateJob(Job job);
  void deleteJob(int id);
}