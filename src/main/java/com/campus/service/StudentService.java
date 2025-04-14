package com.campus.service;

import com.campus.model.Student;
import java.util.List;

public interface StudentService {
  void createStudent(Student student);
  Student getStudentById(int id);
  List<Student> getAllStudents();
  void updateStudent(Student student);
  void deleteStudent(int id);
}