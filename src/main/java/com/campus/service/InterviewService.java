package com.campus.service;

import com.campus.model.Interview;
import java.util.List;

public interface InterviewService {
  void createInterview(Interview interview);
  Interview getInterviewById(int id);
  List<Interview> getAllInterviews();
  void updateInterview(Interview interview);
  void deleteInterview(int id);
}