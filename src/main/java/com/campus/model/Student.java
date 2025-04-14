package com.campus.model;

public class Student {
  private int studentId;
  private String firstName;
  private String lastName;
  private String email;
  private String university;
  private double gpa;

  public int getStudentId() { return studentId; }
  public void setStudentId(int studentId) { this.studentId = studentId; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getUniversity() { return university; }
  public void setUniversity(String university) { this.university = university; }
  public double getGpa() { return gpa; }
  public void setGpa(double gpa) { this.gpa = gpa; }
}