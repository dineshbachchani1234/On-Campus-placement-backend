package com.campus.model;


import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class PlacementReportId implements Serializable {
  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public Integer getCollegeId() {
    return collegeId;
  }

  public void setCollegeId(Integer collegeId) {
    this.collegeId = collegeId;
  }

  private Integer collegeId;
  private Integer year;
}