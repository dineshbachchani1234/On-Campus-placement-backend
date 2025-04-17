package com.campus.repository;

import com.campus.model.Event;
import com.campus.model.EventSponsor; // Import EventSponsor
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(Integer eventId);
    List<Event> findAll();
    boolean update(Event event);
    boolean deleteById(Integer eventId);
    void setEventCompanies(Integer eventId, List<Integer> companyIds);
    void setEventSponsors(Integer eventId, List<EventSponsor> sponsors); // Assuming EventSponsor holds sponsorId and amount
    List<Integer> findCompanyIdsByEventId(Integer eventId); // Helper to get company IDs
    List<EventSponsor> findSponsorsByEventId(Integer eventId); // Helper to get sponsors with amounts
}
