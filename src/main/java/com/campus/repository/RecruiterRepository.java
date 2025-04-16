package com.campus.repository;

import com.campus.model.Recruiter;

import java.util.List;

public interface RecruiterRepository extends BaseRepository<Recruiter, Integer> {
  /**
   * Find recruiters by company ID
   * @param companyId The company ID
   * @return List of recruiters working for the company
   */
  List<Recruiter> findByCompanyId(Integer companyId);

  /**
   * Find recruiters by position
   * @param position The position to search for
   * @return List of recruiters with the specified position
   */
  List<Recruiter> findByPosition(String position);
}