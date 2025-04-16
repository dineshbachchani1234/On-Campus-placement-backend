package com.campus.model;


import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "interviewexperience")
public class InterviewExperience {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "experienceID")
  private Integer experienceId;

  public LocalDate getPostDate() {
    return postDate;
  }

  public void setPostDate(LocalDate postDate) {
    this.postDate = postDate;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Interview getInterview() {
    return interview;
  }

  public void setInterview(Interview interview) {
    this.interview = interview;
  }

  public Integer getExperienceId() {
    return experienceId;
  }

  public void setExperienceId(Integer experienceId) {
    this.experienceId = experienceId;
  }

  @ManyToOne
  @JoinColumn(name = "interviewID", nullable = false)
  private Interview interview;

  @ManyToOne
  @JoinColumn(name = "studentID", nullable = false)
  private Student student;

  @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
  private String comment;

  @Column(name = "rating")
  private Integer rating;

  @Column(name = "postDate", nullable = false)
  private LocalDate postDate;
}