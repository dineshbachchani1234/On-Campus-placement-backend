package com.campus.controller;

import com.campus.model.Student;
import com.campus.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

  @Autowired
  private StudentService studentService;

  @PostMapping
  public String createStudent(@RequestBody Student student) {
    studentService.createStudent(student);
    return "Student created successfully";
  }

  @GetMapping("/{id}")
  public Student getStudentById(@PathVariable int id) {
    return studentService.getStudentById(id);
  }

  @GetMapping
  public List<Student> getAllStudents() {
    return studentService.getAllStudents();
  }

  @PutMapping("/{id}")
  public String updateStudent(@PathVariable int id, @RequestBody Student student) {
    student.setStudentId(id);
    studentService.updateStudent(student);
    return "Student updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteStudent(@PathVariable int id) {
    studentService.deleteStudent(id);
    return "Student deleted successfully";
  }
}