package com.campus.repository;

import com.campus.model.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SponsorRepositoryImpl implements SponsorRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Sponsor sponsor) {
    String sql = "INSERT INTO sponsor (name, contribution_amount, email) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, sponsor.getName(), sponsor.getContributionAmount(), sponsor.getEmail());
  }

  @Override
  public Sponsor findById(int id) {
    String sql = "SELECT * FROM sponsor WHERE sponsor_id = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, new SponsorRowMapper());
  }

  @Override
  public List<Sponsor> findAll() {
    String sql = "SELECT * FROM sponsor";
    return jdbcTemplate.query(sql, new SponsorRowMapper());
  }

  @Override
  public void update(Sponsor sponsor) {
    String sql = "UPDATE sponsor SET name = ?, contribution_amount = ?, email = ? WHERE sponsor_id = ?";
    jdbcTemplate.update(sql, sponsor.getName(), sponsor.getContributionAmount(), sponsor.getEmail(), sponsor.getSponsorId());
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE FROM sponsor WHERE sponsor_id = ?";
    jdbcTemplate.update(sql, id);
  }

  private static class SponsorRowMapper implements RowMapper<Sponsor> {
    @Override
    public Sponsor mapRow(ResultSet rs, int rowNum) throws SQLException {
      Sponsor sponsor = new Sponsor();
      sponsor.setSponsorId(rs.getInt("sponsor_id"));
      sponsor.setName(rs.getString("name"));
      sponsor.setContributionAmount(rs.getDouble("contribution_amount"));
      sponsor.setEmail(rs.getString("email"));
      return sponsor;
    }
  }
}