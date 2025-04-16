package com.campus.model;


import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "company")
public class Company {
  public String getTelephone4() {
    return telephone4;
  }

  public void setTelephone4(String telephone4) {
    this.telephone4 = telephone4;
  }

  public Integer getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getIndustry() {
    return industry;
  }

  public void setIndustry(String industry) {
    this.industry = industry;
  }

  public String getCompanyEmail() {
    return companyEmail;
  }

  public void setCompanyEmail(String companyEmail) {
    this.companyEmail = companyEmail;
  }

  public String getTelephone1() {
    return telephone1;
  }

  public void setTelephone1(String telephone1) {
    this.telephone1 = telephone1;
  }

  public String getTelephone2() {
    return telephone2;
  }

  public void setTelephone2(String telephone2) {
    this.telephone2 = telephone2;
  }

  public String getTelephone3() {
    return telephone3;
  }

  public void setTelephone3(String telephone3) {
    this.telephone3 = telephone3;
  }

  public Set<Recruiter> getRecruiters() {
    return recruiters;
  }

  public void setRecruiters(Set<Recruiter> recruiters) {
    this.recruiters = recruiters;
  }

  public Set<JobListing> getJobListings() {
    return jobListings;
  }

  public void setJobListings(Set<JobListing> jobListings) {
    this.jobListings = jobListings;
  }

  public Set<Event> getEvents() {
    return events;
  }

  public void setEvents(Set<Event> events) {
    this.events = events;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "companyID")
  private Integer companyId;

  @Column(name = "companyName", nullable = false, length = 100, unique = true)
  private String companyName;

  @Column(name = "industry", nullable = false, length = 100)
  private String industry;

  @Column(name = "companyEmail", nullable = false, length = 100, unique = true)
  private String companyEmail;

  @Column(name = "telephone1", nullable = false, length = 20)
  private String telephone1;

  @Column(name = "telephone2", length = 20)
  private String telephone2;

  @Column(name = "telephone3", length = 20)
  private String telephone3;

  @Column(name = "telephone4", length = 20)
  private String telephone4;

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
  private Set<Recruiter> recruiters;

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
  private Set<JobListing> jobListings;

  @ManyToMany(mappedBy = "companies")
  private Set<Event> events;

}