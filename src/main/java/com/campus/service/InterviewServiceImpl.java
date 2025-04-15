package com.campus.service;

import com.campus.model.Interview;
import com.campus.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewServiceImpl implements InterviewService {

  @Autowired
  private InterviewRepository interviewRepository;

  @Override
  public void createInterview(Interview interview) {
    interviewRepository.save(interview);
  }

  @Override
  public Interview getInterviewById(int id) {
    return interviewRepository.findById(id);
  }

  @Override
  public List<Interview> getAllInterviews() {
    return interviewRepository.findAll();
  }

  @Override
  public void updateInterview(Interview interview) {
    interviewRepository.update(interview);
  }

  @Override
  public void deleteInterview(int id) {
    interviewRepository.deleteById(id);
  }
}