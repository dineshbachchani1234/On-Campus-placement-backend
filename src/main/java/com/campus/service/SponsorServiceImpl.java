package com.campus.service;

import com.campus.model.Sponsor;
import com.campus.repository.SponsorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SponsorServiceImpl implements SponsorService {

  @Autowired
  private SponsorRepository sponsorRepository;

  @Override
  public void createSponsor(Sponsor sponsor) {
    sponsorRepository.save(sponsor);
  }

  @Override
  public Sponsor getSponsorById(int id) {
    return sponsorRepository.findById(id);
  }

  @Override
  public List<Sponsor> getAllSponsors() {
    return sponsorRepository.findAll();
  }

  @Override
  public void updateSponsor(Sponsor sponsor) {
    sponsorRepository.update(sponsor);
  }

  @Override
  public void deleteSponsor(int id) {
    sponsorRepository.deleteById(id);
  }
}