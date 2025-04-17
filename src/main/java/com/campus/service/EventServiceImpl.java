package com.campus.service;

import com.campus.model.*; // Keep wildcard for now, add specifics if needed later
import com.campus.repository.*; // Keep wildcard for repositories
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // Add BigDecimal for default sponsor amount
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects; // Add Objects for null checks
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class); // Add logger

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CompanyRepository companyRepository; // Assuming this exists and has save/findById methods

    @Autowired
    private SponsorRepository sponsorRepository; // Assuming this exists and has save/findById methods

    @Autowired
    private AdminRepository adminRepository; // Needed to set the Admin object

    // --- Helper method to get Admin ID (replace with actual security context logic later) ---
    private Integer getCurrentAdminId() {
        // Placeholder: In a real app, get this from Spring Security context
        // For now, let's assume admin with ID 1 is performing the action
        return 1;
    }

    @Transactional // Ensure atomicity
    @Override
    // Update signature to use CompanyInput and SponsorInput
    public Event createEvent(Event event, List<CompanyInput> companyInputs, List<SponsorInput> sponsorInputs) {
        logger.info("Attempting to create event: {}", event.getTitle());
        // 1. Set the Admin based on current context (placeholder logic)
        Integer adminId = getCurrentAdminId();
        logger.debug("Using Admin ID: {}", adminId);
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found for ID: " + adminId));
        event.setAdmin(admin);

        // 2. Save the core event details first to get the event ID
        Event savedEvent = eventRepository.save(event);
        Integer eventId = savedEvent.getEventId();
        logger.debug("Saved core event with ID: {}", eventId);

        // 3. Process and link Companies
        List<Integer> finalCompanyIds = new ArrayList<>();
        if (companyInputs != null) {
            for (CompanyInput input : companyInputs) {
                Integer companyId = input.getCompanyId();
                if (companyId != null) {
                    // Existing company ID provided
                    if (companyRepository.existsById(companyId)) {
                        finalCompanyIds.add(companyId);
                        logger.debug("Linking existing company ID: {}", companyId);
                    } else {
                         logger.warn("Company ID {} provided but not found, skipping.", companyId);
                    }
                } else if (input.getName() != null && !input.getName().trim().isEmpty()) {
                    // New company name provided
                    String companyName = input.getName().trim();
                    // Use findByName as defined in the CompanyRepository interface
                    Optional<Company> existingCompany = companyRepository.findByName(companyName);
                    if (existingCompany.isPresent()) {
                        finalCompanyIds.add(existingCompany.get().getCompanyId());
                        logger.debug("Found existing company by name '{}', linking ID: {}", companyName, existingCompany.get().getCompanyId());
                    } else {
                        // Create new company with placeholders
                        logger.debug("Creating new company with name: {}", companyName);
                        Company newCompany = new Company();
                        newCompany.setCompanyName(companyName);
                        newCompany.setIndustry("Unknown"); // Placeholder
                        newCompany.setCompanyEmail(companyName.replaceAll("\\s+", "").toLowerCase() + "@placeholder.com"); // Placeholder email
                        newCompany.setTelephone1("000-0000"); // Placeholder phone
                        Company savedNewCompany = companyRepository.save(newCompany);
                        finalCompanyIds.add(savedNewCompany.getCompanyId());
                        logger.debug("Created and linked new company ID: {}", savedNewCompany.getCompanyId());
                    }
                }
            }
        }
        // Set the final list of company associations
        eventRepository.setEventCompanies(eventId, finalCompanyIds);
        logger.debug("Set companies for event {}: {}", eventId, finalCompanyIds);


        // 4. Process and link Sponsors
        List<EventSponsor> finalEventSponsors = new ArrayList<>();
        if (sponsorInputs != null) {
            for (SponsorInput input : sponsorInputs) {
                Integer sponsorId = input.getSponsorId();
                Sponsor sponsorToLink = null;

                if (sponsorId != null) {
                    // Existing sponsor ID provided
                    Optional<Sponsor> existingSponsor = sponsorRepository.findById(sponsorId);
                    if (existingSponsor.isPresent()) {
                        sponsorToLink = existingSponsor.get();
                        logger.debug("Linking existing sponsor ID: {}", sponsorId);
                    } else {
                        logger.warn("Sponsor ID {} provided but not found, skipping.", sponsorId);
                    }
                } else if (input.getName() != null && !input.getName().trim().isEmpty()) {
                    // New sponsor name provided
                    String sponsorName = input.getName().trim();
                    Optional<Sponsor> existingSponsor = sponsorRepository.findByName(sponsorName); // Assuming findByName exists
                    if (existingSponsor.isPresent()) {
                        sponsorToLink = existingSponsor.get();
                        logger.debug("Found existing sponsor by name '{}', linking ID: {}", sponsorName, sponsorToLink.getSponsorId());
                    } else {
                        // Create new sponsor with placeholders
                        logger.debug("Creating new sponsor with name: {}", sponsorName);
                        Sponsor newSponsor = new Sponsor();
                        newSponsor.setName(sponsorName);
                        newSponsor.setEmail(sponsorName.replaceAll("\\s+", "").toLowerCase() + "@sponsor.placeholder.com"); // Placeholder email
                        newSponsor.setAmount(BigDecimal.ONE); // Placeholder amount (required > 0)
                        sponsorToLink = sponsorRepository.save(newSponsor);
                        logger.debug("Created new sponsor ID: {}", sponsorToLink.getSponsorId());
                    }
                }

                // If we have a sponsor (existing or new), create the link
                if (sponsorToLink != null) {
                    EventSponsor eventSponsorLink = new EventSponsor();
                    EventSponsorId eventSponsorId = new EventSponsorId(eventId, sponsorToLink.getSponsorId());
                    eventSponsorLink.setId(eventSponsorId);
                    eventSponsorLink.setAmount(BigDecimal.ONE); // Default amount for the event sponsorship link
                    finalEventSponsors.add(eventSponsorLink);
                }
            }
        }
        // Set the final list of sponsor associations
        eventRepository.setEventSponsors(eventId, finalEventSponsors);
        logger.debug("Set sponsors for event {}: {}", eventId, finalEventSponsors.stream().map(es -> es.getId().getSponsorId()).collect(Collectors.toList()));


        // Return the saved event (potentially reload to get linked entities if needed)
        logger.info("Successfully created event ID: {}", eventId);
        return savedEvent; // Consider fetching again if needed: eventRepository.findById(eventId).get();
    }

    @Transactional // Ensure atomicity
    @Override
    // Update signature to use CompanyInput and SponsorInput
    public Event updateEvent(Event event, List<CompanyInput> companyInputs, List<SponsorInput> sponsorInputs) {
        logger.info("Attempting to update event ID: {}", event.getEventId());
        // 1. Verify the event exists
        Integer eventId = event.getEventId();
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // 2. Set the Admin based on current context (placeholder logic)
         Integer adminId = getCurrentAdminId();
         Admin admin = adminRepository.findById(adminId)
                 .orElseThrow(() -> new RuntimeException("Admin not found for ID: " + adminId));
         event.setAdmin(admin); // Ensure admin is set for update

        // 3. Update core event details
        // Assuming eventRepository.update handles the core fields update
        eventRepository.update(event);
        logger.debug("Updated core event details for ID: {}", eventId);

        // 4. Process and update Company links (similar logic to createEvent)
        List<Integer> finalCompanyIds = new ArrayList<>();
        if (companyInputs != null) {
            for (CompanyInput input : companyInputs) {
                Integer companyId = input.getCompanyId();
                if (companyId != null) {
                    if (companyRepository.existsById(companyId)) {
                        finalCompanyIds.add(companyId);
                    } else {
                         logger.warn("Update Event: Company ID {} provided but not found, skipping.", companyId);
                    }
                } else if (input.getName() != null && !input.getName().trim().isEmpty()) {
                    String companyName = input.getName().trim();
                    Optional<Company> existingCompany = companyRepository.findByName(companyName);
                    if (existingCompany.isPresent()) {
                        finalCompanyIds.add(existingCompany.get().getCompanyId());
                    } else {
                        logger.debug("Update Event: Creating new company with name: {}", companyName);
                        Company newCompany = new Company();
                        newCompany.setCompanyName(companyName);
                        newCompany.setIndustry("Unknown");
                        newCompany.setCompanyEmail(companyName.replaceAll("\\s+", "").toLowerCase() + "@placeholder.com");
                        newCompany.setTelephone1("000-0000");
                        Company savedNewCompany = companyRepository.save(newCompany);
                        finalCompanyIds.add(savedNewCompany.getCompanyId());
                        logger.debug("Update Event: Created and linked new company ID: {}", savedNewCompany.getCompanyId());
                    }
                }
            }
        }
        eventRepository.setEventCompanies(eventId, finalCompanyIds);
        logger.debug("Updated companies for event {}: {}", eventId, finalCompanyIds);

        // 5. Process and update Sponsor links (similar logic to createEvent)
        List<EventSponsor> finalEventSponsors = new ArrayList<>();
        if (sponsorInputs != null) {
             for (SponsorInput input : sponsorInputs) {
                 Integer sponsorId = input.getSponsorId();
                 Sponsor sponsorToLink = null;
                 if (sponsorId != null) {
                     Optional<Sponsor> existingSponsor = sponsorRepository.findById(sponsorId);
                     if (existingSponsor.isPresent()) {
                         sponsorToLink = existingSponsor.get();
                     } else {
                         logger.warn("Update Event: Sponsor ID {} provided but not found, skipping.", sponsorId);
                     }
                 } else if (input.getName() != null && !input.getName().trim().isEmpty()) {
                     String sponsorName = input.getName().trim();
                     Optional<Sponsor> existingSponsor = sponsorRepository.findByName(sponsorName);
                     if (existingSponsor.isPresent()) {
                         sponsorToLink = existingSponsor.get();
                     } else {
                         logger.debug("Update Event: Creating new sponsor with name: {}", sponsorName);
                         Sponsor newSponsor = new Sponsor();
                         newSponsor.setName(sponsorName);
                         newSponsor.setEmail(sponsorName.replaceAll("\\s+", "").toLowerCase() + "@sponsor.placeholder.com");
                         newSponsor.setAmount(BigDecimal.ONE);
                         sponsorToLink = sponsorRepository.save(newSponsor);
                         logger.debug("Update Event: Created new sponsor ID: {}", sponsorToLink.getSponsorId());
                     }
                 }

                 if (sponsorToLink != null) {
                     EventSponsor eventSponsorLink = new EventSponsor();
                     EventSponsorId eventSponsorId = new EventSponsorId(eventId, sponsorToLink.getSponsorId());
                     eventSponsorLink.setId(eventSponsorId);
                     eventSponsorLink.setAmount(BigDecimal.ONE); // Default amount
                     finalEventSponsors.add(eventSponsorLink);
                 }
             }
         }
        eventRepository.setEventSponsors(eventId, finalEventSponsors);
        logger.debug("Updated sponsors for event {}: {}", eventId, finalEventSponsors.stream().map(es -> es.getId().getSponsorId()).collect(Collectors.toList()));


        // Return the updated event (fetch again to reflect changes)
        logger.info("Successfully updated event ID: {}", eventId);
        return eventRepository.findById(eventId).orElse(event); // Return updated or original if fetch fails
    }

    @Override
    public Optional<Event> getEventById(Integer eventId) {
        // Fetch event and potentially enrich with company/sponsor details if needed
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        // Enrichment logic could go here if the Event model holds Sets of Companies/Sponsors
        return eventOpt;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteEvent(Integer eventId) {
        // Links in join tables (eventcompany, eventsponsor) should be deleted by CASCADE constraint
        boolean deleted = eventRepository.deleteById(eventId);
        if (!deleted) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }
    }

    @Override
    public List<Company> getCompaniesForEvent(Integer eventId) {
        List<Integer> companyIds = eventRepository.findCompanyIdsByEventId(eventId);
        if (companyIds.isEmpty()) {
            return Collections.emptyList();
        }
        // Assuming CompanyRepository has a findByIdIn or similar method
        // return companyRepository.findAllById(companyIds); // Standard JPA way
         // If using custom repo method:
         return companyRepository.findByIdIn(companyIds); // Assuming this method exists
    }

    @Override
    public List<Sponsor> getSponsorsForEvent(Integer eventId) {
        List<EventSponsor> eventSponsors = eventRepository.findSponsorsByEventId(eventId);
        if (eventSponsors.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> sponsorIds = eventSponsors.stream()
                                                .map(es -> es.getId().getSponsorId())
                                                .collect(Collectors.toList());

        // Fetch Sponsor details
        // Assuming SponsorRepository has findByIdIn or similar
        // List<Sponsor> sponsors = sponsorRepository.findAllById(sponsorIds); // Standard JPA way
        List<Sponsor> sponsors = sponsorRepository.findByIdIn(sponsorIds); // Assuming this method exists

        // Optional: Add the specific amount back to the Sponsor object if needed (requires modifying Sponsor model or using a DTO)
        // Map<Integer, BigDecimal> amountMap = eventSponsors.stream().collect(Collectors.toMap(es -> es.getId().getSponsorId(), EventSponsor::getAmount));
        // sponsors.forEach(s -> s.setSponsoredAmount(amountMap.get(s.getSponsorId()))); // Example if Sponsor had setSponsoredAmount

        return sponsors;
    }
}
