package com.campus.model;

public class Sponsor {
  private int sponsorId;
  private String name;
  private double contributionAmount;
  private String email;

  public int getSponsorId() { return sponsorId; }
  public void setSponsorId(int sponsorId) { this.sponsorId = sponsorId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public double getContributionAmount() { return contributionAmount; }
  public void setContributionAmount(double contributionAmount) { this.contributionAmount = contributionAmount; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
