package com.campus.repository;


import com.campus.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends BaseRepository<Company, Integer> {
  Company findById(int id);

  void deleteById(int id);

  /**
   * Find a company by name
   * @param companyName The company name
   * @return Optional containing the company if found
   */
  Optional<Company> findByName(String companyName);

  /**
   * Find companies by industry
   * @param industry The industry to search for
   * @return List of companies in the industry
   */
  List<Company> findByIndustry(String industry);

  /**
   * Find companies by email
   * @param email The email to search for
   * @return Optional containing the company if found
   */
  Optional<Company> findByEmail(String email);

  /**
   * Check if a company name already exists
   * @param companyName The company name to check
   * @return true if the company name exists, false otherwise
   */
  boolean existsByName(String companyName);

  /**
   * Check if a company email already exists
   * @param email The email to check
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);
}