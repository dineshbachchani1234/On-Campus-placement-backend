package com.campus.model;


import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "admin")
public class Admin {
  public Set<Event> getEvents() {
    return events;
  }

  public void setEvents(Set<Event> events) {
    this.events = events;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Integer getAdminId() {
    return adminId;
  }

  public void setAdminId(Integer adminId) {
    this.adminId = adminId;
  }

  @Id
  @Column(name = "adminID")
  private Integer adminId;

  @OneToOne
  @JoinColumn(name = "adminID", referencedColumnName = "userID")
  @MapsId
  private User user;

  @OneToMany(mappedBy = "admin")
  private Set<Event> events;
}