package com.campus.service;

import com.campus.model.Admin;
import com.campus.model.College;
import com.campus.model.Company;
import com.campus.model.JwtAuthResponse;
import com.campus.model.LoginRequest;
import com.campus.model.Recruiter;
import com.campus.model.SignupRequest;
import com.campus.model.Student;
import com.campus.model.User;
import com.campus.repository.AdminRepository;
import com.campus.repository.CollegeRepository;
import com.campus.repository.CompanyRepository;
import com.campus.repository.RecruiterRepository;
import com.campus.repository.StudentRepository;
import com.campus.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private RecruiterRepository recruiterRepository;

  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private CollegeRepository collegeRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public JwtAuthResponse login(LoginRequest loginRequest) {
    // Authenticate with Spring Security
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Get user from database
    Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

    if (!userOptional.isPresent()) {
      throw new RuntimeException("User not found");
    }

    User user = userOptional.get();

    // Generate JWT token
    String token = tokenProvider.generateToken(user);

    return new JwtAuthResponse(token, user.getUserId(), user.getEmail(), user.getRole());
  }

  @Override
  @Transactional
  public User register(SignupRequest signupRequest) {
    // Check if email exists
    if (existsByEmail(signupRequest.getEmail())) {
      throw new RuntimeException("Email already exists");
    }

    // Create user
    User user = new User();
    user.setFirstName(signupRequest.getFirstName());
    user.setLastName(signupRequest.getLastName());
    user.setEmail(signupRequest.getEmail());
    user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
    user.setRole(signupRequest.getRole());

    user = userRepository.save(user);

    // Create role-specific entity
    switch (user.getRole()) {
      case STUDENT:
        createStudent(user, signupRequest);
        break;
      case RECRUITER:
        createRecruiter(user, signupRequest);
        break;
      case ADMIN:
        createAdmin(user);
        break;
    }

    return user;
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  private void createStudent(User user, SignupRequest signupRequest) {
    Student student = new Student();
    student.setStudentId(user.getUserId());
    student.setUser(user);

    // Find college
    Optional<College> collegeOptional = collegeRepository.findById(signupRequest.getCollegeId());
//    if (!collegeOptional.isPresent()) {
//      throw new RuntimeException("College not found");
//    }

    College college = new College();
    college.setName("a");
    college.setCollegeId(1);
    student.setCollege(college);
    student.setMajor("CS");
    student.setGpa(new BigDecimal("4.0"));
    student.setResume("resume");
    student.setPlaced(false);
    student.setTotalApplicationsCount(0);

    studentRepository.save(student);
  }

  private void createRecruiter(User user, SignupRequest signupRequest) {
    Recruiter recruiter = new Recruiter();
    recruiter.setRecruiterId(user.getUserId());
    recruiter.setUser(user);

    // Find company
    Optional<Company> companyOptional = companyRepository.findById(signupRequest.getCompanyId());
    if (!companyOptional.isPresent()) {
      throw new RuntimeException("Company not found");
    }

    recruiter.setCompany(companyOptional.get());
    recruiter.setPosition(signupRequest.getPosition());

    recruiterRepository.save(recruiter);
  }

  private void createAdmin(User user) {
    Admin admin = new Admin();
    admin.setAdminId(user.getUserId());
    admin.setUser(user);

    adminRepository.save(admin);
  }
}