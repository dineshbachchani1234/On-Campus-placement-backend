package com.campus.repository;

import com.campus.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;



  @Override
  public Company findById(int id) {
    String sql = "SELECT * FROM company WHERE companyID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new CompanyRowMapper());
  }

  @Override
  public List<Company> findAll() {
    String sql = "SELECT * FROM company";
    return jdbcTemplate.query(sql, new CompanyRowMapper());
  }

  @Override
  public Company save(Company entity) {
    String sql = "INSERT INTO company (name, industry, email) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, entity.getCompanyName(), entity.getIndustry(), entity.getCompanyEmail());
    return null;
  }

  @Override
  public Company update(Company company) {
    String sql = "UPDATE company SET name = ?, industry = ?, email = ? WHERE companyID = ?";
    jdbcTemplate.update(sql, company.getCompanyName(), company.getIndustry(), company.getCompanyEmail(), company.getCompanyId());
    return null;
  }

  @Override
  public boolean deleteById(Integer integer) {
    return false;
  }

  @Override
  public Optional<Company> findById(Integer integer) {
    String sql = "SELECT * FROM company WHERE companyID = ?";
    try {
      Company company = jdbcTemplate.queryForObject(
          sql,
          new Object[]{integer},
          new CompanyRowMapper()
      );
      return Optional.ofNullable(company);
    } catch (EmptyResultDataAccessException ex) {
      // No row found for this id
      return Optional.empty();
    }
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM company WHERE companyID = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public Optional<Company> findByName(String companyName) {
    return Optional.empty();
  }

  @Override
  public List<Company> findByIndustry(String industry) {
    //return List.of();
    return null;
  }

  @Override
  public Optional<Company> findByEmail(String email) {
    return Optional.empty();
  }

  @Override
  public boolean existsByName(String companyName) {
    return false;
  }

  @Override
  public boolean existsByEmail(String email) {
    return false;
  }

  private static class CompanyRowMapper implements RowMapper<Company> {
    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
      Company company = new Company();
      company.setCompanyId(rs.getInt("companyID"));
      company.setCompanyName(rs.getString("companyName"));
      company.setIndustry(rs.getString("industry"));
      company.setCompanyEmail(rs.getString("companyEmail"));
      return company;
    }
  }
}
