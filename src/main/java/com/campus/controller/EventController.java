package com.campus.controller;

import com.campus.model.*; // Import all models, including new DTOs and wrappers
import com.campus.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // Import java.time.LocalDate
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow CORS requests
public class EventController {

    @Autowired
    private EventService eventService;

    // --- Endpoints ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequestDto requestDto) {
        try {
            // 1. Map DTO to Event object
            Event event = new Event();
            event.setTitle(requestDto.getTitle());
            event.setDescription(requestDto.getDescription());
            event.setLocation(requestDto.getLocation());
            // Convert String date "YYYY-MM-DD" to java.time.LocalDate
            if (requestDto.getDate() != null) {
                event.setDate(LocalDate.parse(requestDto.getDate())); // Use LocalDate.parse
            }
            // Note: Admin is set in the service layer

            // 2. Get CompanyInput and SponsorInput lists directly from DTO
            List<CompanyInput> companyInputs = requestDto.getCompanies(); // Ensure this returns List<CompanyInput>
            List<SponsorInput> sponsorInputs = requestDto.getSponsors();   // Ensure this returns List<SponsorInput>

            // 3. Call the updated service method
            Event createdEvent = eventService.createEvent(event, companyInputs, sponsorInputs); // Pass the correct types
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) { // Catch potential Date parsing errors or other service layer validation
            // Log error (Consider adding logging)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Log general error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'RECRUITER')") // Allow all roles to view event details
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        Optional<Event> eventData = eventService.getEventById(id);
        // TODO: Potentially enrich with company/sponsor data before returning
        return eventData.map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
     // @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'RECRUITER')") // Allow all roles to view event list
    public ResponseEntity<List<Event>> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            if (events.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(@PathVariable Integer id, @RequestBody EventRequestDto requestDto) {
        try {
            // 1. Map DTO to Event object
            Event event = new Event();
            event.setEventId(id); // Set ID from path variable
            event.setTitle(requestDto.getTitle());
            event.setDescription(requestDto.getDescription());
            event.setLocation(requestDto.getLocation());
            if (requestDto.getDate() != null) {
                event.setDate(LocalDate.parse(requestDto.getDate())); // Use LocalDate.parse
            }
            // Admin is set in the service layer

            // 2. Get CompanyInput and SponsorInput lists directly from DTO
            List<CompanyInput> companyInputs = requestDto.getCompanies(); // Ensure this returns List<CompanyInput>
            List<SponsorInput> sponsorInputs = requestDto.getSponsors();   // Ensure this returns List<SponsorInput>

            // 3. Call the updated service method
            Event updatedEvent = eventService.updateEvent(event, companyInputs, sponsorInputs); // Pass the correct types
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } catch (IllegalArgumentException e) { // Catch potential Date parsing errors or other service layer validation
            // Log error (Consider adding logging)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) { // Catch specific exceptions like "Not Found" from service
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Log general error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only Admins can delete events
    public ResponseEntity<HttpStatus> deleteEvent(@PathVariable Integer id) {
        try {
            eventService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) { // Catch specific exceptions like "Not Found"
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Optional: Endpoints to get companies/sponsors for a specific event
    @GetMapping("/{id}/companies")
    public ResponseEntity<List<Company>> getEventCompanies(@PathVariable Integer id) {
        try {
            List<Company> companies = eventService.getCompaniesForEvent(id);
             return new ResponseEntity<>(companies, HttpStatus.OK);
        } catch (Exception e) {
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     @GetMapping("/{id}/sponsors")
     public ResponseEntity<List<Sponsor>> getEventSponsors(@PathVariable Integer id) {
         try {
             List<Sponsor> sponsors = eventService.getSponsorsForEvent(id);
              return new ResponseEntity<>(sponsors, HttpStatus.OK);
         } catch (Exception e) {
              return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }

}
