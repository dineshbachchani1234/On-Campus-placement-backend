package com.campus.model;


public class SignupRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private User.Role role;

  public Integer getCollegeId() {
    return collegeId;
  }

  public void setCollegeId(Integer collegeId) {
    this.collegeId = collegeId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public User.Role getRole() {
    return role;
  }

  public void setRole(User.Role role) {
    this.role = role;
  }

  public String getMajor() {
    return major;
  }

  public void setMajor(String major) {
    this.major = major;
  }

  public String getGpa() {
    return gpa;
  }

  public void setGpa(String gpa) {
    this.gpa = gpa;
  }

  public String getResume() {
    return resume;
  }

  public void setResume(String resume) {
    this.resume = resume;
  }

  public Integer getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  // Student specific fields
  private Integer collegeId;
  private String major;
  private String gpa;
  private String resume;

  // Recruiter specific fields
  private Integer companyId;
  private String position;
}