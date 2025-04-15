package com.campus.repository;

import com.campus.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Company company) {
    String sql = "INSERT INTO company (name, industry, email) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, company.getName(), company.getIndustry(), company.getEmail());
  }

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
  public void update(Company company) {
    String sql = "UPDATE company SET name = ?, industry = ?, email = ? WHERE companyID = ?";
    jdbcTemplate.update(sql, company.getName(), company.getIndustry(), company.getEmail(), company.getCompanyId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM company WHERE companyID = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class CompanyRowMapper implements RowMapper<Company> {
    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
      Company company = new Company();
      company.setCompanyId(rs.getInt("companyID"));
      company.setName(rs.getString("name"));
      company.setIndustry(rs.getString("industry"));
      company.setEmail(rs.getString("email"));
      return company;
    }
  }
}
