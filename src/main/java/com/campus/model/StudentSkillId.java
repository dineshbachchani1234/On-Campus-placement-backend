package com.campus.model;


import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class StudentSkillId implements Serializable {
  private Integer studentId;

  public Integer getStudentId() {
    return studentId;
  }

  public void setStudentId(Integer studentId) {
    this.studentId = studentId;
  }

  public Integer getSkillId() {
    return skillId;
  }

  public void setSkillId(Integer skillId) {
    this.skillId = skillId;
  }

  private Integer skillId;
}
