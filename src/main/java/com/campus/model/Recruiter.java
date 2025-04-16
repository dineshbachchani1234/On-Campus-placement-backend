package com.campus.model;




import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "recruiter")
public class Recruiter {
  @Id
  @Column(name = "recruiterID")
  private Integer recruiterId;

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Integer getRecruiterId() {
    return recruiterId;
  }

  public void setRecruiterId(Integer recruiterId) {
    this.recruiterId = recruiterId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public Set<Interview> getInterviews() {
    return interviews;
  }

  public void setInterviews(Set<Interview> interviews) {
    this.interviews = interviews;
  }

  @OneToOne
  @JoinColumn(name = "recruiterID", referencedColumnName = "userID")
  @MapsId
  private User user;

  @ManyToOne
  @JoinColumn(name = "companyID", nullable = false)
  private Company company;

  @Column(name = "position", nullable = false, length = 100)
  private String position;

  @OneToMany(mappedBy = "recruiter")
  private Set<Interview> interviews;
}