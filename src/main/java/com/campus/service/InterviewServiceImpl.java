package com.campus.service;

import com.campus.model.Application;
import com.campus.model.Interview;
import com.campus.model.InterviewExperience;
import com.campus.repository.ApplicationRepository;
import com.campus.repository.InterviewExperienceRepository;
import com.campus.repository.InterviewRepository;
import com.campus.service.InterviewService;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewServiceImpl implements InterviewService {

  @Autowired
  private InterviewRepository interviewRepository;

  @Autowired
  private InterviewExperienceRepository experienceRepository;

  @Autowired
  private ApplicationRepository applicationRepository;

  @Override
  public Interview scheduleInterview(Interview interview) {
    // Validate application
    Optional<Application> applicationOptional = applicationRepository.findById(
        interview.getApplication().getApplicationId());
    if (!applicationOptional.isPresent()) {
      throw new RuntimeException("Application not found");
    }

    Application application = applicationOptional.get();

    // Only schedule interviews for applications with SHORTLISTED status
    if (application.getStatus() != Application.ApplicationStatus.SHORTLISTED) {
      throw new RuntimeException("Cannot schedule interview for non-shortlisted application");
    }

    // Set default values if not provided
    if (interview.getStatus() == null) {
      interview.setStatus(Interview.InterviewStatus.SCHEDULED);
    }

    if (interview.getResult() == null) {
      interview.setResult(Interview.InterviewResult.PENDING);
    }

    // Save interview
    Interview savedInterview = interviewRepository.save(interview);

    // Update application status
    applicationRepository.updateStatus(application.getApplicationId(),
        Application.ApplicationStatus.INTERVIEWED);

    return savedInterview;
  }

  @Override
  public Optional<Interview> getInterviewById(Integer id) {
    return interviewRepository.findById(id);
  }

  @Override
  public List<Interview> getInterviewsByApplicationId(Integer applicationId) {
    return interviewRepository.findByApplicationId(applicationId);
  }

  @Override
  public List<Interview> getInterviewsByRecruiterId(Integer recruiterId) {
    return interviewRepository.findByRecruiterId(recruiterId);
  }

  @Override
  public List<Interview> getInterviewsByStatus(Interview.InterviewStatus status) {
    return interviewRepository.findByStatus(status);
  }

  @Override
  public List<Interview> getInterviewsByResult(Interview.InterviewResult result) {
    return interviewRepository.findByResult(result);
  }

  @Override
  public List<Interview> getUpcomingInterviews() {
    return interviewRepository.findUpcomingInterviews(LocalDateTime.now());
  }

  @Override
  public boolean updateInterviewStatus(Integer interviewId, Interview.InterviewStatus status) {
    return interviewRepository.updateStatus(interviewId, status);
  }

  @Override
  public boolean updateInterviewResult(Integer interviewId, Interview.InterviewResult result, String feedback) {
    Optional<Interview> interviewOptional = interviewRepository.findById(interviewId);
    if (!interviewOptional.isPresent()) {
      return false;
    }

    Interview interview = interviewOptional.get();

    // Only update result for completed interviews
    if (interview.getStatus() != Interview.InterviewStatus.COMPLETED) {
      throw new RuntimeException("Cannot update result for non-completed interview");
    }

    boolean updated = interviewRepository.updateResult(interviewId, result, feedback);

    // If result is SELECTED, update application status to OFFERED
    if (updated && result == Interview.InterviewResult.SELECTED) {
      applicationRepository.updateStatus(interview.getApplication().getApplicationId(),
          Application.ApplicationStatus.OFFERED);
    } else if (updated && result == Interview.InterviewResult.REJECTED) {
      applicationRepository.updateStatus(interview.getApplication().getApplicationId(),
          Application.ApplicationStatus.REJECTED);
    }

    return updated;
  }

  @Override
  public InterviewExperience addInterviewExperience(InterviewExperience experience) {
    // Set post date if not provided
    if (experience.getPostDate() == null) {
      experience.setPostDate(LocalDate.now());
    }

    // Validate rating
    if (experience.getRating() != null && (experience.getRating() < 1 || experience.getRating() > 5)) {
      throw new RuntimeException("Rating must be between 1 and 5");
    }

    return experienceRepository.save(experience);
  }

  @Override
  public List<InterviewExperience> getExperiencesByInterviewId(Integer interviewId) {
    return experienceRepository.findByInterviewId(interviewId);
  }

  @Override
  public boolean cancelInterview(Integer interviewId) {
    return interviewRepository.updateStatus(interviewId, Interview.InterviewStatus.CANCELLED);
  }

  @Override
  public boolean rescheduleInterview(Integer interviewId, LocalDateTime newDateTime) {
    Optional<Interview> interviewOptional = interviewRepository.findById(interviewId);
    if (!interviewOptional.isPresent()) {
      return false;
    }

    Interview interview = interviewOptional.get();

    // Only reschedule scheduled interviews
    if (interview.getStatus() != Interview.InterviewStatus.SCHEDULED) {
      throw new RuntimeException("Cannot reschedule non-scheduled interview");
    }

    interview.setInterviewDate(newDateTime);
    interviewRepository.update(interview);

    return true;
  }

  @Override
  public List<Interview> getInterviewsByStudentId(Integer studentId) {
    // First, get all applications for this student
    List<Application> applications = applicationRepository.findByStudentId(studentId);

    // Then get all interviews for these applications
    List<Interview> interviews = new ArrayList<>();
    for (Application application : applications) {
      List<Interview> appInterviews = interviewRepository.findByStudentId(studentId);
      interviews.addAll(appInterviews);
    }

    return interviews;
  }

  @Override
  public List<InterviewExperience> getAllInterviewExperiences() {
    return experienceRepository.findAllExperiences();
  }
}