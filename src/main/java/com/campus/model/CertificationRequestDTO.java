package com.campus.model;

public class CertificationRequestDTO {

  private String name;
  private String issuingOrganization;
  private String certificationDate; // Expecting YYYY-MM-DD format
  private String expiryDate;        // Optional, YYYY-MM-DD format
  private Integer credentialId;      // Optional

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

  public String getCertificationDate() {
    return certificationDate;
  }

  public void setCertificationDate(String certificationDate) {
    this.certificationDate = certificationDate;
  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Integer getCredentialId() {
    return credentialId;
  }

  public void setCredentialId(Integer credentialId) {
    this.credentialId = credentialId;
  }
}
