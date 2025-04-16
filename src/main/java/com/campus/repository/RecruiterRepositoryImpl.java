package com.campus.repository;

import com.campus.model.Company;
import com.campus.model.Recruiter;
import com.campus.repository.CompanyRepository;
import com.campus.repository.RecruiterRepository;
import com.campus.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RecruiterRepositoryImpl implements RecruiterRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private RowMapper<Recruiter> recruiterRowMapper = (rs, rowNum) -> {
    Recruiter recruiter = new Recruiter();
    recruiter.setRecruiterId(rs.getInt("recruiterID"));

    Company company = new Company();
    company.setCompanyId(rs.getInt("companyID"));
    recruiter.setCompany(company);

    recruiter.setPosition(rs.getString("position"));

    // Load the user and company
    userRepository.findById(recruiter.getRecruiterId()).ifPresent(recruiter::setUser);
    companyRepository.findById(company.getCompanyId()).ifPresent(recruiter::setCompany);

    return recruiter;
  };

  @Override
  public Recruiter save(Recruiter recruiter) {
    String sql = "INSERT INTO recruiter (recruiterID, companyID, position) VALUES (?, ?, ?)";

    jdbcTemplate.update(sql,
        recruiter.getRecruiterId(),
        recruiter.getCompany().getCompanyId(),
        recruiter.getPosition()
    );

    return recruiter;
  }

  @Override
  public Recruiter update(Recruiter recruiter) {
    String sql = "UPDATE recruiter SET companyID = ?, position = ? WHERE recruiterID = ?";

    jdbcTemplate.update(sql,
        recruiter.getCompany().getCompanyId(),
        recruiter.getPosition(),
        recruiter.getRecruiterId()
    );

    return recruiter;
  }

  @Override
  public boolean deleteById(Integer id) {
    String sql = "DELETE FROM recruiter WHERE recruiterID = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }

  @Override
  public Optional<Recruiter> findById(Integer id) {
    String sql = "SELECT * FROM recruiter WHERE recruiterID = ?";

    try {
      Recruiter recruiter = jdbcTemplate.queryForObject(sql, recruiterRowMapper, id);
      return Optional.ofNullable(recruiter);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Recruiter> findAll() {
    String sql = "SELECT * FROM recruiter";
    return jdbcTemplate.query(sql, recruiterRowMapper);
  }

  @Override
  public List<Recruiter> findByCompanyId(Integer companyId) {
    String sql = "SELECT * FROM recruiter WHERE companyID = ?";
    return jdbcTemplate.query(sql, recruiterRowMapper, companyId);
  }

  @Override
  public List<Recruiter> findByPosition(String position) {
    String sql = "SELECT * FROM recruiter WHERE position = ?";
    return jdbcTemplate.query(sql, recruiterRowMapper, position);
  }
}