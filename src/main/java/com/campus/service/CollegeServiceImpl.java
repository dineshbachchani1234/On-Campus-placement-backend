package com.campus.service;

import com.campus.model.College;
import com.campus.repository.CollegeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollegeServiceImpl implements CollegeService {

  @Autowired
  private CollegeRepository repo;

  @Override
  public List<College> getAllColleges() {
    return repo.findAll();
  }

  @Override
  public Optional<College> getCollegeById(int collegeID) {
    return repo.findById(collegeID);
  }
}
