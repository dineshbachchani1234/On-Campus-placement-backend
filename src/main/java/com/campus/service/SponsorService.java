package com.campus.service;

import com.campus.model.Sponsor;
import java.util.List;

public interface SponsorService {
  void createSponsor(Sponsor sponsor);
  Sponsor getSponsorById(int id);
  List<Sponsor> getAllSponsors();
  void updateSponsor(Sponsor sponsor);
  void deleteSponsor(int id);
}
