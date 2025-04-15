package com.campus.service;

import com.campus.model.Recruiter;
import java.util.List;

public interface RecruiterService {
  void createRecruiter(Recruiter recruiter);
  Recruiter getRecruiterById(int id);
  List<Recruiter> getAllRecruiters();
  void updateRecruiter(Recruiter recruiter);
  void deleteRecruiter(int id);
}