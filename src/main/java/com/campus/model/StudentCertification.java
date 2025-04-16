package com.campus.model;


import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "studentcertification")
public class StudentCertification {
  @EmbeddedId
  private StudentCertificationId id;

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }

  public StudentCertificationId getId() {
    return id;
  }

  public void setId(StudentCertificationId id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Certification getCertification() {
    return certification;
  }

  public void setCertification(Certification certification) {
    this.certification = certification;
  }

  public LocalDate getCertificationDate() {
    return certificationDate;
  }

  public void setCertificationDate(LocalDate certificationDate) {
    this.certificationDate = certificationDate;
  }

  public String getCredentialId() {
    return credentialId;
  }

  public void setCredentialId(String credentialId) {
    this.credentialId = credentialId;
  }

  @ManyToOne
  @MapsId("studentId")
  @JoinColumn(name = "studentID")
  private Student student;

  @ManyToOne
  @MapsId("certificationId")
  @JoinColumn(name = "certificationID")
  private Certification certification;

  @Column(name = "certificationDate", nullable = false)
  private LocalDate certificationDate;

  @Column(name = "expiryDate")
  private LocalDate expiryDate;

  @Column(name = "credentialID", length = 100)
  private String credentialId;
}