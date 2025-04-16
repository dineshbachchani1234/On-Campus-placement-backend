package com.campus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "event")
public class CareerFair {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "eventID")
  private int fairId;
  private String title;
  private String description;
  private String date;
  private String location;

  public int getFairId() { return fairId; }
  public void setFairId(int fairId) { this.fairId = fairId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  public String getLocation() { return location; }
  public void setLocation(String location) { this.location = location; }
}