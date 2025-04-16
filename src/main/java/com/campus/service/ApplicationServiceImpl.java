package com.campus.service;

import com.campus.model.Application;
import com.campus.model.JobListing;
import com.campus.repository.ApplicationRepository;
import com.campus.repository.JobListingRepository;
import com.campus.service.ApplicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationServiceImpl implements ApplicationService {

  @Autowired
  private ApplicationRepository applicationRepository;

  @Autowired
  private JobListingRepository jobListingRepository;

  @Override
  public Application submitApplication(Application application) {
    // Check if the student has already applied to this job
    if (hasStudentAppliedToJob(application.getStudent().getStudentId(), application.getJob().getJobId())) {
      throw new RuntimeException("You have already applied to this job");
    }

    // Check if the job is active and deadline has not passed
    Optional<JobListing> jobOptional = jobListingRepository.findById(application.getJob().getJobId());
    if (!jobOptional.isPresent()) {
      throw new RuntimeException("Job not found");
    }

    JobListing job = jobOptional.get();
    if (!job.getActive()) {
      throw new RuntimeException("This job is no longer active");
    }

    if (job.getDeadline().isBefore(LocalDate.now())) {
      throw new RuntimeException("Application deadline has passed");
    }

    // Set default values if not provided
    if (application.getApplicationDate() == null) {
      application.setApplicationDate(LocalDate.now());
    }

    if (application.getStatus() == null) {
      application.setStatus(Application.ApplicationStatus.PENDING);
    }

    return applicationRepository.save(application);
  }

  @Override
  public Optional<Application> getApplicationById(Integer id) {
    return applicationRepository.findById(id);
  }

  @Override
  public List<Application> getApplicationsByStudentId(Integer studentId) {
    return applicationRepository.findByStudentId(studentId);
  }

  @Override
  public List<Application> getApplicationsByJobId(Integer jobId) {
    return applicationRepository.findByJobId(jobId);
  }

  @Override
  public List<Application> getApplicationsByStatus(Application.ApplicationStatus status) {
    return applicationRepository.findByStatus(status);
  }

  @Override
  public boolean updateApplicationStatus(Integer applicationId, Application.ApplicationStatus status) {
    return applicationRepository.updateStatus(applicationId, status);
  }

  @Override
  public boolean hasStudentAppliedToJob(Integer studentId, Integer jobId) {
    return applicationRepository.existsByStudentIdAndJobId(studentId, jobId);
  }

  @Override
  public boolean withdrawApplication(Integer applicationId) {
    Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
    if (!applicationOptional.isPresent()) {
      return false;
    }

    Application application = applicationOptional.get();

    // Can only withdraw if status is PENDING
    if (application.getStatus() != Application.ApplicationStatus.PENDING) {
      return false;
    }

    return applicationRepository.deleteById(applicationId);
  }
}