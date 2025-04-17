package com.campus.service;

import com.campus.model.CareerFair;
import com.campus.model.Event;
import java.util.List;

public interface CareerFairService {
  void createCareerFair(Event fair);
  CareerFair getCareerFairById(int id);
  List<CareerFair> getAllCareerFairs();
  void updateCareerFair(CareerFair fair);
  void deleteCareerFair(int id);
}