package com.campus.model;



import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "college")
public class College {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "collegeID")
  private Integer collegeId;

  @Column(name = "name", nullable = false, length = 100, unique = true)
  private String name;

  @Column(name = "numberOfStudents", columnDefinition = "INT DEFAULT 0")
  private Integer numberOfStudents = 0;

  @OneToMany(mappedBy = "college")
  private Set<Student> students;

  @OneToMany(mappedBy = "college", cascade = CascadeType.ALL)
  private Set<PlacementReport> placementReports;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCollegeId() {
    return collegeId;
  }

  public void setCollegeId(Integer collegeId) {
    this.collegeId = collegeId;
  }

  public Integer getNumberOfStudents() {
    return numberOfStudents;
  }

  public void setNumberOfStudents(Integer numberOfStudents) {
    this.numberOfStudents = numberOfStudents;
  }

  public Set<Student> getStudents() {
    return students;
  }

  public void setStudents(Set<Student> students) {
    this.students = students;
  }

  public Set<PlacementReport> getPlacementReports() {
    return placementReports;
  }

  public void setPlacementReports(Set<PlacementReport> placementReports) {
    this.placementReports = placementReports;
  }
}