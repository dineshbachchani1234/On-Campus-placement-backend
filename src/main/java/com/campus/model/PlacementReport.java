package com.campus.model;


import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "placementreport")
public class PlacementReport {
  @EmbeddedId
  private PlacementReportId id;

  @ManyToOne
  @MapsId("collegeId")
  @JoinColumn(name = "collegeID")
  private College college;

  public College getCollege() {
    return college;
  }

  public void setCollege(College college) {
    this.college = college;
  }

  public PlacementReportId getId() {
    return id;
  }

  public void setId(PlacementReportId id) {
    this.id = id;
  }

  public Integer getPlacedStudents() {
    return placedStudents;
  }

  public void setPlacedStudents(Integer placedStudents) {
    this.placedStudents = placedStudents;
  }

  public Integer getTotalStudents() {
    return totalStudents;
  }

  public void setTotalStudents(Integer totalStudents) {
    this.totalStudents = totalStudents;
  }

  public LocalDate getReportDate() {
    return reportDate;
  }

  public void setReportDate(LocalDate reportDate) {
    this.reportDate = reportDate;
  }

  @Column(name = "placedStudents", nullable = false)
  private Integer placedStudents = 0;

  @Column(name = "totalStudents", nullable = false)
  private Integer totalStudents = 0;

  @Column(name = "reportDate", nullable = false)
  private LocalDate reportDate;
}