package com.campus.service;

import com.campus.model.Job;
import com.campus.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

  @Autowired
  private JobRepository jobRepository;

  @Override
  public void createJob(Job job) {
    jobRepository.save(job);
  }

  @Override
  public Job getJobById(int id) {
    return jobRepository.findById(id);
  }

  @Override
  public List<Job> getAllJobs() {
    return jobRepository.findAll();
  }

  @Override
  public void updateJob(Job job) {
    jobRepository.update(job);
  }

  @Override
  public void deleteJob(int id) {
    jobRepository.deleteById(id);
  }
}