package com.campus.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userID")
  private Integer userId;

  @Column(name = "firstName", nullable = false, length = 50)
  private String firstName;

  @Column(name = "lastName", nullable = false, length = 50)
  private String lastName;

  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  // Enum for user roles
  public enum Role {
    STUDENT, ADMIN, RECRUITER
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}