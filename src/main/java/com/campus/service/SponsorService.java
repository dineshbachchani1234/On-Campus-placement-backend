package com.campus.service;

import com.campus.model.Sponsor;
import java.util.List;
import java.util.Optional; // Import Optional

public interface SponsorService {
  void createSponsor(Sponsor sponsor);
  Optional<Sponsor> getSponsorById(int id); // Change return type
  List<Sponsor> getAllSponsors();
  void updateSponsor(Sponsor sponsor);
  void deleteSponsor(int id);
}
