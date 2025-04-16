package com.campus.model;


import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "skill")
public class Skill {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "skillID")
  private Integer skillId;

  public Set<Student> getStudents() {
    return students;
  }

  public void setStudents(Set<Student> students) {
    this.students = students;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSkillName() {
    return skillName;
  }

  public void setSkillName(String skillName) {
    this.skillName = skillName;
  }

  public Integer getSkillId() {
    return skillId;
  }

  public void setSkillId(Integer skillId) {
    this.skillId = skillId;
  }

  @Column(name = "skillName", nullable = false, length = 100, unique = true)
  private String skillName;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @ManyToMany(mappedBy = "skills")
  private Set<Student> students;
}
