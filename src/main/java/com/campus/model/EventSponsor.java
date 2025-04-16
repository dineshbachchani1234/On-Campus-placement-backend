package com.campus.model;


import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "eventsponsor")
public class EventSponsor {
  @EmbeddedId
  private EventSponsorId id;

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public EventSponsorId getId() {
    return id;
  }

  public void setId(EventSponsorId id) {
    this.id = id;
  }

  public Sponsor getSponsor() {
    return sponsor;
  }

  public void setSponsor(Sponsor sponsor) {
    this.sponsor = sponsor;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @ManyToOne
  @MapsId("eventId")
  @JoinColumn(name = "eventID")
  private Event event;

  @ManyToOne
  @MapsId("sponsorId")
  @JoinColumn(name = "sponsorID")
  private Sponsor sponsor;

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;
}