package com.campus.service;

import com.campus.model.Company;
import java.util.List;

public interface CompanyService {
  void createCompany(Company company);
  Company getCompanyById(int id);
  List<Company> getAllCompanies();
  void updateCompany(Company company);
  void deleteCompany(int id);
}