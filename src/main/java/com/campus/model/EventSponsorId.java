package com.campus.model;


import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class EventSponsorId implements Serializable {
  private Integer eventId;

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

  private Integer sponsorId;
}