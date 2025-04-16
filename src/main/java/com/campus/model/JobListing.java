package com.campus.model;




import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;


@Entity
@Table(name = "joblisting")
public class JobListing {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "jobID")
  private Integer jobId;

  @ManyToOne
  @JoinColumn(name = "companyID", nullable = false)
  private Company company;

  public JobType getJobType() {
    return jobType;
  }

  public void setJobType(JobType jobType) {
    this.jobType = jobType;
  }

  public Integer getJobId() {
    return jobId;
  }

  public void setJobId(Integer jobId) {
    this.jobId = jobId;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getSalary() {
    return salary;
  }

  public void setSalary(BigDecimal salary) {
    this.salary = salary;
  }

  public LocalDate getDeadline() {
    return deadline;
  }

  public void setDeadline(LocalDate deadline) {
    this.deadline = deadline;
  }

  public LocalDate getPostDate() {
    return postDate;
  }

  public void setPostDate(LocalDate postDate) {
    this.postDate = postDate;
  }

  public Boolean getActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }

  public Set<Application> getApplications() {
    return applications;
  }

  public void setApplications(Set<Application> applications) {
    this.applications = applications;
  }

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "salary", precision = 10, scale = 2)
  private BigDecimal salary;

  @Enumerated(EnumType.STRING)
  @Column(name = "jobType", nullable = false)
  private JobType jobType;

  @Column(name = "deadline", nullable = false)
  private LocalDate deadline;

  @Column(name = "postDate", nullable = false)
  private LocalDate postDate;

  @Column(name = "isActive", columnDefinition = "TINYINT(1) DEFAULT 1")
  private Boolean isActive = true;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
  private Set<Application> applications;

  // Enum for job types
  public enum JobType {
    INTERNSHIP, FULL_TIME, PART_TIME
  }
}