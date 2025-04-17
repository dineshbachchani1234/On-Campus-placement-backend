package com.campus.model;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = "interview")
public class Interview {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "interviewID")
  private Integer interviewId;

  @ManyToOne
  @JoinColumn(name = "applicationID", nullable = false)
  private Application application;

  public JobOffer getOffer() {
    return offer;
  }

  public void setOffer(JobOffer offer) {
    this.offer = offer;
  }

  public Integer getInterviewId() {
    return interviewId;
  }

  public void setInterviewId(Integer interviewId) {
    this.interviewId = interviewId;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public Recruiter getRecruiter() {
    return recruiter;
  }

  public void setRecruiter(Recruiter recruiter) {
    this.recruiter = recruiter;
  }

  public LocalDateTime getInterviewDate() {
    return interviewDate;
  }

  public void setInterviewDate(LocalDateTime interviewDate) {
    this.interviewDate = interviewDate;
  }

  public InterviewStatus getStatus() {
    return status;
  }

  public void setStatus(InterviewStatus status) {
    this.status = status;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  public InterviewResult getResult() {
    return result;
  }

  public void setResult(InterviewResult result) {
    this.result = result;
  }

  public Set<InterviewExperience> getExperiences() {
    return experiences;
  }

  public void setExperiences(Set<InterviewExperience> experiences) {
    this.experiences = experiences;
  }

  // Getter and Setter for notes
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  @ManyToOne
  @JoinColumn(name = "recruiterID", nullable = false)
  private Recruiter recruiter;

  @Column(name = "interviewDate", nullable = false)
  private LocalDateTime interviewDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private InterviewStatus status = InterviewStatus.SCHEDULED;

  @Column(name = "feedback", columnDefinition = "TEXT")
  private String feedback;

  @Column(name = "notes", columnDefinition = "TEXT") // Added notes field
  private String notes;

  @Enumerated(EnumType.STRING)
  @Column(name = "result")
  private InterviewResult result = InterviewResult.PENDING;

  @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL)
  private JobOffer offer;

  @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
  private Set<InterviewExperience> experiences;

  // Enum for interview status
  public enum InterviewStatus {
    SCHEDULED, COMPLETED, CANCELLED
  }

  // Enum for interview result
  public enum InterviewResult {
    PENDING, SELECTED, REJECTED
  }
}
