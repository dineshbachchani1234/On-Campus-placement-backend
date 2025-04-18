package com.campus.service;

import com.campus.model.College;
import java.util.List;
import java.util.Optional;

public interface CollegeService {

  List<College> getAllColleges();
  Optional<College> getCollegeById(int collegeID);

}
