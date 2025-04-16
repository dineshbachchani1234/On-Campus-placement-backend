package com.campus.model;



import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;


@Entity
@Table(name = "application")
public class Application {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "applicationID")
  private Integer applicationId;

  @ManyToOne
  @JoinColumn(name = "studentID", nullable = false)
  private Student student;

  public ApplicationStatus getStatus() {
    return status;
  }

  public void setStatus(ApplicationStatus status) {
    this.status = status;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public JobListing getJob() {
    return job;
  }

  public void setJob(JobListing job) {
    this.job = job;
  }

  public LocalDate getApplicationDate() {
    return applicationDate;
  }

  public void setApplicationDate(LocalDate applicationDate) {
    this.applicationDate = applicationDate;
  }

  public Set<Interview> getInterviews() {
    return interviews;
  }

  public void setInterviews(Set<Interview> interviews) {
    this.interviews = interviews;
  }

  @ManyToOne
  @JoinColumn(name = "jobID", nullable = false)
  private JobListing job;

  @Column(name = "applicationDate", nullable = false)
  private LocalDate applicationDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private ApplicationStatus status = ApplicationStatus.PENDING;

  @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
  private Set<Interview> interviews;

  // Enum for application status
  public enum ApplicationStatus {
    PENDING, SHORTLISTED, REJECTED, INTERVIEWED, OFFERED, ACCEPTED, DECLINED
  }
}