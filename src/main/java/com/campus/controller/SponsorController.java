package com.campus.controller;

import com.campus.model.Sponsor;
import com.campus.service.SponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sponsors")
public class SponsorController {

  @Autowired
  private SponsorService sponsorService;

  @PostMapping
  public String createSponsor(@RequestBody Sponsor sponsor) {
    sponsorService.createSponsor(sponsor);
    return "Sponsor created successfully";
  }

  @GetMapping("/{id}")
  public Sponsor getSponsorById(@PathVariable int id) {
    return sponsorService.getSponsorById(id);
  }

  @GetMapping
  public List<Sponsor> getAllSponsors() {
    return sponsorService.getAllSponsors();
  }

  @PutMapping("/{id}")
  public String updateSponsor(@PathVariable int id, @RequestBody Sponsor sponsor) {
    sponsor.setSponsorId(id);
    sponsorService.updateSponsor(sponsor);
    return "Sponsor updated successfully";
  }

  @DeleteMapping("/{id}")
  public String deleteSponsor(@PathVariable int id) {
    sponsorService.deleteSponsor(id);
    return "Sponsor deleted successfully";
  }
}
