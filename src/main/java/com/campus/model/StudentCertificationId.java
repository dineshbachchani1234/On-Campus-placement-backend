package com.campus.model;


import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class StudentCertificationId implements Serializable {
  public Integer getStudentId() {
    return studentId;
  }

  public void setStudentId(Integer studentId) {
    this.studentId = studentId;
  }

  public Integer getCertificationId() {
    return certificationId;
  }

  public void setCertificationId(Integer certificationId) {
    this.certificationId = certificationId;
  }

  private Integer studentId;
  private Integer certificationId;
}
