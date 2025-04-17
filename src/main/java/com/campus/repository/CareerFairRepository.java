package com.campus.repository;

import com.campus.model.CareerFair;
import com.campus.model.Event;
import java.util.List;

public interface CareerFairRepository {
  void save(Event fair);
  CareerFair findById(int id);
  List<CareerFair> findAll();
  void update(CareerFair fair);
  void deleteById(int id);
}