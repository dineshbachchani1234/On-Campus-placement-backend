package com.campus.service;

import com.campus.model.Application;
import java.util.List;

public interface ApplicationService {
  void createApplication(Application application);
  Application getApplicationById(int id);
  List<Application> getAllApplications();
  void updateApplication(Application application);
  void deleteApplication(int id);
}