package com.campus.service;

import com.campus.model.Company;
import com.campus.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

  @Autowired
  private CompanyRepository companyRepository;

  @Override
  public void createCompany(Company company) {
    companyRepository.save(company);
  }

  @Override
  public Company getCompanyById(int id) {
    return companyRepository.findById(id);
  }

  @Override
  public List<Company> getAllCompanies() {
    return companyRepository.findAll();
  }

  @Override
  public void updateCompany(Company company) {
    companyRepository.update(company);
  }

  @Override
  public void deleteCompany(int id) {
    companyRepository.deleteById(id);
  }
}