package com.campus.model;


import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "joboffer")
public class JobOffer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "offerID")
  private Integer offerId;

  @OneToOne
  @JoinColumn(name = "interviewID", nullable = false, unique = true)
  private Interview interview;

  @Column(name = "offerDate", nullable = false)
  private LocalDate offerDate;

  public OfferResult getResult() {
    return result;
  }

  public void setResult(OfferResult result) {
    this.result = result;
  }

  public Integer getOfferId() {
    return offerId;
  }

  public void setOfferId(Integer offerId) {
    this.offerId = offerId;
  }

  public Interview getInterview() {
    return interview;
  }

  public void setInterview(Interview interview) {
    this.interview = interview;
  }

  public LocalDate getOfferDate() {
    return offerDate;
  }

  public void setOfferDate(LocalDate offerDate) {
    this.offerDate = offerDate;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "result")
  private OfferResult result = OfferResult.PENDING;

  @Column(name = "feedback", columnDefinition = "TEXT")
  private String feedback;

  // Enum for offer result
  public enum OfferResult {
    PENDING, ACCEPTED, DECLINED
  }
}