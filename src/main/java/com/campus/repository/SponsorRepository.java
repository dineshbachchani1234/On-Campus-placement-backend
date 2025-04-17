package com.campus.repository;

import com.campus.model.Sponsor;
import java.util.List;
import java.util.Optional;

public interface SponsorRepository {
    Sponsor save(Sponsor sponsor);
    Optional<Sponsor> findById(Integer sponsorId);
    List<Sponsor> findAll();
    boolean update(Sponsor sponsor);
    boolean deleteById(Integer sponsorId);
    Optional<Sponsor> findByEmail(String email); // Added based on unique constraint
    Optional<Sponsor> findByName(String name); // Add findByName
    List<Sponsor> findByIdIn(List<Integer> sponsorIds); // Added for EventService
}
