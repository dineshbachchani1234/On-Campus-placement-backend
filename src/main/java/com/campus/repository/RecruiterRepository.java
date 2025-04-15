package com.campus.repository;

import com.campus.model.Recruiter;
import java.util.List;

public interface RecruiterRepository {
  void save(Recruiter recruiter);
  Recruiter findById(int id);
  List<Recruiter> findAll();
  void update(Recruiter recruiter);
  void deleteById(int id);
}