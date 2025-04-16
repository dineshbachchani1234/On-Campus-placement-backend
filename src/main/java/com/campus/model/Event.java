package com.campus.model;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;


@Entity
@Table(name = "event")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "eventID")
  private Integer eventId;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Integer getEventId() {
    return eventId;
  }

  public void setEventId(Integer eventId) {
    this.eventId = eventId;
  }

  public Admin getAdmin() {
    return admin;
  }

  public void setAdmin(Admin admin) {
    this.admin = admin;
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

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Set<Company> getCompanies() {
    return companies;
  }

  public void setCompanies(Set<Company> companies) {
    this.companies = companies;
  }

  public Set<Sponsor> getSponsors() {
    return sponsors;
  }

  public void setSponsors(Set<Sponsor> sponsors) {
    this.sponsors = sponsors;
  }

  @ManyToOne
  @JoinColumn(name = "adminID", nullable = false)
  private Admin admin;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "location", nullable = false, length = 255)
  private String location;

  @ManyToMany
  @JoinTable(
      name = "eventcompany",
      joinColumns = @JoinColumn(name = "eventID"),
      inverseJoinColumns = @JoinColumn(name = "companyID")
  )
  private Set<Company> companies;

  @ManyToMany
  @JoinTable(
      name = "eventsponsor",
      joinColumns = @JoinColumn(name = "eventID"),
      inverseJoinColumns = @JoinColumn(name = "sponsorID")
  )
  private Set<Sponsor> sponsors;
}