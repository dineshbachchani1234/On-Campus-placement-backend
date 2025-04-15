package com.campus.controller;

import com.campus.model.Company;
import com.campus.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

  @Autowired
  private CompanyService companyService;

  @PostMapping
  public String createCompany(@RequestBody Company company) {
    companyService.createCompany(company);
    return "Company created successfully";
  }

  @GetMapping("/{id}")
  public Company getCompanyById(@PathVariable int id) {
    return companyService.getCompanyById(id);
  }

  @GetMapping
  public List<Company> getAllCompanies() {
    return companyService.getAllCompanies();
  }

  @PutMapping("/{id}")
  public String updateCompany(@PathVariable int id, @RequestBody Company company) {
    company.setCompanyId(id);
    companyService.updateCompany(company);
    return "Company updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteCompany(@PathVariable int id) {
    companyService.deleteCompany(id);
    return "Company deleted successfully";
  }
}
