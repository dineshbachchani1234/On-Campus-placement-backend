package com.campus.model;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "sponsor")
public class Sponsor {
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Integer getSponsorId() {
    return sponsorId;
  }

  public void setSponsorId(Integer sponsorId) {
    this.sponsorId = sponsorId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Set<Event> getEvents() {
    return events;
  }

  public void setEvents(Set<Event> events) {
    this.events = events;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sponsorID")
  private Integer sponsorId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @ManyToMany(mappedBy = "sponsors")
  private Set<Event> events;
}