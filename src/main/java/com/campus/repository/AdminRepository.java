package com.campus.repository;

import com.campus.model.Admin;

import java.util.List;

/**
 * Repository for Admin operations
 */
public interface AdminRepository extends BaseRepository<Admin, Integer> {

  /**
   * Execute stored procedure to generate placement report
   * @param collegeId The college ID
   * @param year The year for the report
   * @return true if successful, false otherwise
   */
  boolean generatePlacementReport(Integer collegeId, Integer year);

  /**
   * Get placement rate using database function
   * @param collegeId The college ID
   * @param year The year for the report
   * @return placement rate as a percentage
   */
  double getPlacementRate(Integer collegeId, Integer year);
}