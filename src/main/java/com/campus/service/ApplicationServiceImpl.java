package com.campus.service;

import com.campus.model.Application;
import com.campus.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

  @Autowired
  private ApplicationRepository applicationRepository;

  @Override
  public void createApplication(Application application) {
    applicationRepository.save(application);
  }

  @Override
  public Application getApplicationById(int id) {
    return applicationRepository.findById(id);
  }

  @Override
  public List<Application> getAllApplications() {
    return applicationRepository.findAll();
  }

  @Override
  public void updateApplication(Application application) {
    applicationRepository.update(application);
  }

  @Override
  public void deleteApplication(int id) {
    applicationRepository.deleteById(id);
  }
}