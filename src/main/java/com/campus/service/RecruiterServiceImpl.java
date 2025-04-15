package com.campus.service;

import com.campus.model.Recruiter;
import com.campus.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecruiterServiceImpl implements RecruiterService {

  @Autowired
  private RecruiterRepository recruiterRepository;

  @Override
  public void createRecruiter(Recruiter recruiter) {
    recruiterRepository.save(recruiter);
  }

  @Override
  public Recruiter getRecruiterById(int id) {
    return recruiterRepository.findById(id);
  }

  @Override
  public List<Recruiter> getAllRecruiters() {
    return recruiterRepository.findAll();
  }

  @Override
  public void updateRecruiter(Recruiter recruiter) {
    recruiterRepository.update(recruiter);
  }

  @Override
  public void deleteRecruiter(int id) {
    recruiterRepository.deleteById(id);
  }
}