package com.campus.controller;

import com.campus.model.CareerFair;
import com.campus.service.CareerFairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careerfairs")
public class CareerFairController {

  @Autowired
  private CareerFairService careerFairService;

  @PostMapping
  public String createCareerFair(@RequestBody CareerFair fair) {
    careerFairService.createCareerFair(fair);
    return "Career fair created successfully";
  }

  @GetMapping("/{id}")
  public CareerFair getCareerFairById(@PathVariable int id) {
    return careerFairService.getCareerFairById(id);
  }

  @GetMapping
  public List<CareerFair> getAllCareerFairs() {
    return careerFairService.getAllCareerFairs();
  }

  @PutMapping("/{id}")
  public String updateCareerFair(@PathVariable int id, @RequestBody CareerFair fair) {
    fair.setFairId(id);
    careerFairService.updateCareerFair(fair);
    return "Career fair updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteCareerFair(@PathVariable int id) {
    careerFairService.deleteCareerFair(id);
    return "Career fair deleted successfully";
  }
}