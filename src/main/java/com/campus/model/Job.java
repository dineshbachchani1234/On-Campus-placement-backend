package com.campus.model;

public class Job {
  private int jobId;
  private String title;
  private String description;
  private double salary;
  private String jobType;

  public int getJobId() { return jobId; }
  public void setJobId(int jobId) { this.jobId = jobId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public double getSalary() { return salary; }
  public void setSalary(double salary) { this.salary = salary; }
  public String getJobType() { return jobType; }
  public void setJobType(String jobType) { this.jobType = jobType; }
}