package com.campus.repository;

import com.campus.model.Sponsor;
import java.util.List;

public interface SponsorRepository {
  void save(Sponsor sponsor);
  Sponsor findById(int id);
  List<Sponsor> findAll();
  void update(Sponsor sponsor);
  void deleteById(int id);
}