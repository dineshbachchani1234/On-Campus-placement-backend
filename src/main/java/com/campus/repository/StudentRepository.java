package com.campus.repository;

import com.campus.model.Student;
import java.util.List;

public interface StudentRepository {
  void save(Student student);
  Student findById(int id);
  List<Student> findAll();
  void update(Student student);
  void deleteById(int id);
}