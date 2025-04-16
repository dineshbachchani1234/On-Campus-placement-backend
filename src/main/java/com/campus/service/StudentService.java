package com.campus.service;

import com.campus.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
  void createStudent(Student student);
  Optional<Student> getStudentById(int id);
  List<Student> getAllStudents();
  void updateStudent(Student student);
  void deleteStudent(int id);
}