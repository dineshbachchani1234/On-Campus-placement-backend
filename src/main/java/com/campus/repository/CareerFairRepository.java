package com.campus.repository;

import com.campus.model.CareerFair;
import java.util.List;

public interface CareerFairRepository {
  void save(CareerFair fair);
  CareerFair findById(int id);
  List<CareerFair> findAll();
  void update(CareerFair fair);
  void deleteById(int id);
}