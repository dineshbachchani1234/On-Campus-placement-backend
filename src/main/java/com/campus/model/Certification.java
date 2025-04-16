package com.campus.model;


import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "certification")
public class Certification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "certificationID")
  private Integer certificationId;

  public Integer getCertificationId() {
    return certificationId;
  }

  public void setCertificationId(Integer certificationId) {
    this.certificationId = certificationId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIssuingOrganization() {
    return issuingOrganization;
  }

  public void setIssuingOrganization(String issuingOrganization) {
    this.issuingOrganization = issuingOrganization;
  }

  public Set<Student> getStudents() {
    return students;
  }

  public void setStudents(Set<Student> students) {
    this.students = students;
  }

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "issuingOrganization", nullable = false, length = 100)
  private String issuingOrganization;

  @ManyToMany(mappedBy = "certifications")
  private Set<Student> students;
}