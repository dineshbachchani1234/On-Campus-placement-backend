package com.campus.model;

public class Application {
  private int applicationId;
  private int studentId;
  private int jobId;
  private String applicationDate;
  private String status;

  public int getApplicationId() { return applicationId; }
  public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
  public int getStudentId() { return studentId; }
  public void setStudentId(int studentId) { this.studentId = studentId; }
  public int getJobId() { return jobId; }
  public void setJobId(int jobId) { this.jobId = jobId; }
  public String getApplicationDate() { return applicationDate; }
  public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}
