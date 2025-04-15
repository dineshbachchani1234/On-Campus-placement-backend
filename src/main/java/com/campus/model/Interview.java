package com.campus.model;

public class Interview {
  private int interviewId;
  private int applicationId;
  private String interviewDate;
  private String feedback;
  private String result;

  public int getInterviewId() { return interviewId; }
  public void setInterviewId(int interviewId) { this.interviewId = interviewId; }
  public int getApplicationId() { return applicationId; }
  public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
  public String getInterviewDate() { return interviewDate; }
  public void setInterviewDate(String interviewDate) { this.interviewDate = interviewDate; }
  public String getFeedback() { return feedback; }
  public void setFeedback(String feedback) { this.feedback = feedback; }
  public String getResult() { return result; }
  public void setResult(String result) { this.result = result; }
}