package com.campus.service;

import com.campus.model.CareerFair;
import com.campus.repository.CareerFairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerFairServiceImpl implements CareerFairService {

  @Autowired
  private CareerFairRepository careerFairRepository;

  @Override
  public void createCareerFair(CareerFair fair) {
    careerFairRepository.save(fair);
  }

  @Override
  public CareerFair getCareerFairById(int id) {
    return careerFairRepository.findById(id);
  }

  @Override
  public List<CareerFair> getAllCareerFairs() {
    return careerFairRepository.findAll();
  }

  @Override
  public void updateCareerFair(CareerFair fair) {
    careerFairRepository.update(fair);
  }

  @Override
  public void deleteCareerFair(int id) {
    careerFairRepository.deleteById(id);
  }
}