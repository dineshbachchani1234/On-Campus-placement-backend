package com.campus.service;

// Import necessary models, including the new Input DTOs
import com.campus.model.Company;
import com.campus.model.CompanyInput; // Explicit import
import com.campus.model.Event;
import com.campus.model.Sponsor;
import com.campus.model.SponsorInput; // Explicit import

import java.util.List;
import java.util.Optional;

public interface EventService {

    // Updated to accept CompanyInput and SponsorInput
    Event createEvent(Event event, List<CompanyInput> companyInputs, List<SponsorInput> sponsorInputs);

    // Updated to accept CompanyInput and SponsorInput
    Event updateEvent(Event event, List<CompanyInput> companyInputs, List<SponsorInput> sponsorInputs);

    Optional<Event> getEventById(Integer eventId);

    List<Event> getAllEvents();

    void deleteEvent(Integer eventId);

    // Methods to get associated companies/sponsors might be useful
    List<Company> getCompaniesForEvent(Integer eventId);
    List<Sponsor> getSponsorsForEvent(Integer eventId); // Might need to return Sponsor + Amount

}
