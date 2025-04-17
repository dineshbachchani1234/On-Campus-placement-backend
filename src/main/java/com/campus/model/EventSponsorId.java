package com.campus.model;


import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class EventSponsorId implements Serializable {
  private Integer eventId;
  private Integer sponsorId; // Moved declaration up

  // Default constructor (required by JPA)
  public EventSponsorId() {
  }

  // Constructor with arguments (needed by EventServiceImpl)
  public EventSponsorId(Integer eventId, Integer sponsorId) {
      this.eventId = eventId;
      this.sponsorId = sponsorId;
  }

  // Getters and Setters
  public Integer getSponsorId() {
    return sponsorId;
  }

  public void setSponsorId(Integer sponsorId) {
    this.sponsorId = sponsorId;
  }

  public Integer getEventId() {
    return eventId;
  }

  public void setEventId(Integer eventId) {
    this.eventId = eventId;
  }

  // Removed duplicate declaration of sponsorId
}
