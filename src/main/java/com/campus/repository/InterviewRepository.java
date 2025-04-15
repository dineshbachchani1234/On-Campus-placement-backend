package com.campus.repository;

import com.campus.model.Interview;
import java.util.List;

public interface InterviewRepository {
  void save(Interview interview);
  Interview findById(int id);
  List<Interview> findAll();
  void update(Interview interview);
  void deleteById(int id);
}