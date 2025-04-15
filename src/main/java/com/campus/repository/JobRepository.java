package com.campus.repository;

import com.campus.model.Job;
import java.util.List;

public interface JobRepository {
  void save(Job job);
  Job findById(int id);
  List<Job> findAll();
  void update(Job job);
  void deleteById(int id);
}