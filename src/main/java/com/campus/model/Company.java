package com.campus.model;

public class Company {
  private int companyId;
  private String name;
  private String industry;
  private String email;

  public int getCompanyId() { return companyId; }
  public void setCompanyId(int companyId) { this.companyId = companyId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getIndustry() { return industry; }
  public void setIndustry(String industry) { this.industry = industry; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}