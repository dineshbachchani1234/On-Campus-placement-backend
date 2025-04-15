package com.campus.repository;

import com.campus.model.Application;
import java.util.List;

public interface ApplicationRepository {
  void save(Application application);
  Application findById(int id);
  List<Application> findAll();
  void update(Application application);
  void deleteById(int id);
}