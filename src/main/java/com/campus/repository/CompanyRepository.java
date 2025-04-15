package com.campus.repository;

import com.campus.model.Company;
import java.util.List;

public interface CompanyRepository {
  void save(Company company);
  Company findById(int id);
  List<Company> findAll();
  void update(Company company);
  void deleteById(int id);
}