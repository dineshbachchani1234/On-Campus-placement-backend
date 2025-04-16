package com.campus.model;


import javax.persistence.*;


@Entity
@Table(name = "studentskill")
public class StudentSkill {
  @EmbeddedId
  private StudentSkillId id;

  public ProficiencyLevel getProficiencyLevel() {
    return proficiencyLevel;
  }

  public void setProficiencyLevel(ProficiencyLevel proficiencyLevel) {
    this.proficiencyLevel = proficiencyLevel;
  }

  public Skill getSkill() {
    return skill;
  }

  public void setSkill(Skill skill) {
    this.skill = skill;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public StudentSkillId getId() {
    return id;
  }

  public void setId(StudentSkillId id) {
    this.id = id;
  }

  @ManyToOne
  @MapsId("studentId")
  @JoinColumn(name = "studentID")
  private Student student;

  @ManyToOne
  @MapsId("skillId")
  @JoinColumn(name = "skillID")
  private Skill skill;

  @Enumerated(EnumType.STRING)
  @Column(name = "proficiencyLevel", nullable = false)
  private ProficiencyLevel proficiencyLevel = ProficiencyLevel.BEGINNER;

  // Enum for proficiency level
  public enum ProficiencyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
  }
}