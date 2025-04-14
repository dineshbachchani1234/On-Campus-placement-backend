package com.campus.service;

import com.campus.model.Student;
import com.campus.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

  @Autowired
  private StudentRepository studentRepository;

  @Override
  public void createStudent(Student student) {
    studentRepository.save(student);
  }

  @Override
  public Student getStudentById(int id) {
    return studentRepository.findById(id);
  }

  @Override
  public List<Student> getAllStudents() {
    return studentRepository.findAll();
  }

  @Override
  public void updateStudent(Student student) {
    studentRepository.update(student);
  }

  @Override
  public void deleteStudent(int id) {
    studentRepository.deleteById(id);
  }
}