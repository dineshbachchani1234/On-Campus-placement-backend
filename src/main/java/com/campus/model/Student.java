package com.campus.model;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "student")
public class Student {
  @Id
  @Column(name = "studentID")
  private Integer studentId;

  @OneToOne
  @JoinColumn(name = "studentID", referencedColumnName = "userID")
  @MapsId
  private User user;

  @ManyToOne
  @JoinColumn(name = "collegeID", nullable = false)
  private College college;

  @Column(name = "major", nullable = false, length = 100)
  private String major;

  @Column(name = "gpa", nullable = false, precision = 3, scale = 2)
  private BigDecimal gpa;

  @Column(name = "resume", columnDefinition = "TEXT")
  private String resume;

  @Column(name = "isPlaced", columnDefinition = "TINYINT(1) DEFAULT 0")
  private Boolean isPlaced = false;

  @Column(name = "totalApplicationsCount", columnDefinition = "INT DEFAULT 0")
  private Integer totalApplicationsCount = 0;

  @OneToMany(mappedBy = "student")
  private Set<Application> applications;

  @ManyToMany
  @JoinTable(
      name = "studentskill",
      joinColumns = @JoinColumn(name = "studentID"),
      inverseJoinColumns = @JoinColumn(name = "skillID")
  )
  private Set<Skill> skills;

  @ManyToMany
  @JoinTable(
      name = "studentcertification",
      joinColumns = @JoinColumn(name = "studentID"),
      inverseJoinColumns = @JoinColumn(name = "certificationID")
  )
  private Set<Certification> certifications;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Integer getStudentId() {
    return studentId;
  }

  public void setStudentId(Integer studentId) {
    this.studentId = studentId;
  }

  public College getCollege() {
    return college;
  }

  public void setCollege(College college) {
    this.college = college;
  }

  public String getMajor() {
    return major;
  }

  public void setMajor(String major) {
    this.major = major;
  }

  public BigDecimal getGpa() {
    return gpa;
  }

  public void setGpa(BigDecimal gpa) {
    this.gpa = gpa;
  }

  public String getResume() {
    return resume;
  }

  public void setResume(String resume) {
    this.resume = resume;
  }

  public Boolean getPlaced() {
    return isPlaced;
  }

  public void setPlaced(Boolean placed) {
    isPlaced = placed;
  }

  public Integer getTotalApplicationsCount() {
    return totalApplicationsCount;
  }

  public void setTotalApplicationsCount(Integer totalApplicationsCount) {
    this.totalApplicationsCount = totalApplicationsCount;
  }

  public Set<Application> getApplications() {
    return applications;
  }

  public void setApplications(Set<Application> applications) {
    this.applications = applications;
  }

  public Set<Skill> getSkills() {
    return skills;
  }

  public void setSkills(Set<Skill> skills) {
    this.skills = skills;
  }

  public Set<Certification> getCertifications() {
    return certifications;
  }

  public void setCertifications(Set<Certification> certifications) {
    this.certifications = certifications;
  }
}