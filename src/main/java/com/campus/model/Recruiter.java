package com.campus.model;

public class Recruiter {
  private int recruiterId;
  private String firstName;
  private String lastName;
  private String email;
  private String position;

  public int getRecruiterId() { return recruiterId; }
  public void setRecruiterId(int recruiterId) { this.recruiterId = recruiterId; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPosition() { return position; }
  public void setPosition(String position) { this.position = position; }
}