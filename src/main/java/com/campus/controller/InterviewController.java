package com.campus.controller;

import com.campus.model.*; // Import all models including DTO
import com.campus.service.InterviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Removed: import org.springframework.security.access.prepost.PreAuthorize;
// Removed: import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// Removed: import javax.validation.Valid; // Removed as per user request
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller for interview operations
 */
@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "*")
public class InterviewController {

  @Autowired
  private InterviewService interviewService;

  /**
   * Schedule a new interview using details from the DTO.
   * @param interviewRequest DTO containing jobId, studentId, recruiterId, dateTime, notes.
   * @return The scheduled Interview object with HTTP status 201 (Created).
   */
  @PostMapping
  // Removed: @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  // Removed Authentication parameter, removed @Valid
  public ResponseEntity<?> scheduleInterview(@RequestBody InterviewRequestDTO interviewRequest) {
    try {
      // Get recruiterId directly from the DTO
      Integer recruiterId = interviewRequest.getRecruiterId();
      if (recruiterId == null) {
          // Handle missing recruiterId if it's considered mandatory
          return ResponseEntity.badRequest()
              .body(MessageResponse.error("Recruiter ID is missing in the request."));
      }

      // Call the updated service method
      Interview scheduledInterview = interviewService.scheduleInterview(
          interviewRequest.getJobId(),
          interviewRequest.getStudentId(),
          recruiterId, // Pass the recruiter ID from the DTO
          interviewRequest.getDateTime(),
          interviewRequest.getNotes()
      );
      // Return 201 Created status with the created interview object
      return ResponseEntity.status(HttpStatus.CREATED).body(scheduledInterview);
    } catch (RuntimeException e) { // Catch specific exceptions if needed
      // Log the error server-side
      System.err.println("Error scheduling interview: " + e.getMessage());
      return ResponseEntity.badRequest()
          .body(MessageResponse.error("Failed to schedule interview: " + e.getMessage()));
    } catch (Exception e) {
       System.err.println("Unexpected error scheduling interview: " + e.getMessage());
       return ResponseEntity.internalServerError()
           .body(MessageResponse.error("An unexpected error occurred."));
    }
  }

  /**
   * Get interview by ID
   * @param id The interview ID
   * @return Interview if found
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getInterviewById(@PathVariable Integer id) {
    Optional<Interview> interview = interviewService.getInterviewById(id);

    if (interview.isPresent()) {
      return ResponseEntity.ok(interview.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get interviews by application ID
   * @param applicationId The application ID
   * @return List of interviews for the application
   */
  @GetMapping("/application/{applicationId}")
  public ResponseEntity<List<Interview>> getInterviewsByApplicationId(@PathVariable Integer applicationId) {
    List<Interview> interviews = interviewService.getInterviewsByApplicationId(applicationId);
    return ResponseEntity.ok(interviews);
  }

  /**
   * Get interviews by recruiter ID
   * @param recruiterId The recruiter ID
   * @return List of interviews conducted by the recruiter
   */
  @GetMapping("/recruiter/{recruiterId}")
  // Removed: @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<List<Interview>> getInterviewsByRecruiterId(@PathVariable Integer recruiterId) {
    List<Interview> interviews = interviewService.getInterviewsByRecruiterId(recruiterId);
    return ResponseEntity.ok(interviews);
  }

  /**
   * Get interviews by status
   * @param status The interview status
   * @return List of interviews with the specified status
   */
  @GetMapping("/status/{status}")
  // Removed: @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<List<Interview>> getInterviewsByStatus(@PathVariable Interview.InterviewStatus status) {
    List<Interview> interviews = interviewService.getInterviewsByStatus(status);
    return ResponseEntity.ok(interviews);
  }

  /**
   * Get interviews by result
   * @param result The interview result
   * @return List of interviews with the specified result
   */
  @GetMapping("/result/{result}")
  // Removed: @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<List<Interview>> getInterviewsByResult(@PathVariable Interview.InterviewResult result) {
    List<Interview> interviews = interviewService.getInterviewsByResult(result);
    return ResponseEntity.ok(interviews);
  }

  /**
   * Get upcoming interviews
   * @return List of upcoming interviews
   */
  @GetMapping("/upcoming")
  public ResponseEntity<List<Interview>> getUpcomingInterviews() {
    List<Interview> interviews = interviewService.getUpcomingInterviews();
    return ResponseEntity.ok(interviews);
  }

  /**
   * Update interview status
   * @param id The interview ID
   * @param status The new status
   * @return Success message
   */
  @PutMapping("/{id}/status")
  // Removed: @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> updateInterviewStatus(
      @PathVariable Integer id,
      @RequestParam Interview.InterviewStatus status) {

    boolean updated = interviewService.updateInterviewStatus(id, status);

    if (updated) {
      return ResponseEntity.ok(MessageResponse.success("Interview status updated successfully"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Update interview result and feedback
   * @param id The interview ID
   * @param result The new result
   * @param feedback The feedback
   * @return Success message
   */
  @PutMapping("/{id}/result")
  // Removed: @PreAuthorize("hasRole('RECRUITER')")
  public ResponseEntity<?> updateInterviewResult(
      @PathVariable Integer id,
      @RequestParam Interview.InterviewResult result,
      @RequestParam(required = false) String feedback) {

    try {
      boolean updated = interviewService.updateInterviewResult(id, result, feedback);

      if (updated) {
        return ResponseEntity.ok(MessageResponse.success("Interview result updated successfully"));
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  /**
   * Add interview experience
   * @param experience The interview experience to add
   * @return Added interview experience
   */
  @PostMapping("/experiences")
  // Removed: @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<?> addInterviewExperience(@RequestBody InterviewExperience experience) {
    try {
      InterviewExperience addedExperience = interviewService.addInterviewExperience(experience);
      return ResponseEntity.ok(addedExperience);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  /**
   * Get experiences for an interview
   * @param interviewId The interview ID
   * @return List of experiences for the interview
   */
  @GetMapping("/{interviewId}/experiences")
  public ResponseEntity<List<InterviewExperience>> getExperiencesByInterviewId(@PathVariable Integer interviewId) {
    List<InterviewExperience> experiences = interviewService.getExperiencesByInterviewId(interviewId);
    return ResponseEntity.ok(experiences);
  }

  /**
   * Cancel interview
   * @param id The interview ID
   * @return Success message
   */
  @PutMapping("/{id}/cancel")
  // Removed: @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> cancelInterview(@PathVariable Integer id) {
    boolean cancelled = interviewService.cancelInterview(id);

    if (cancelled) {
      return ResponseEntity.ok(MessageResponse.success("Interview cancelled successfully"));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Reschedule interview
   * @param id The interview ID
   * @param newDateTime The new date and time
   * @return Success message
   */
  @PutMapping("/{id}/reschedule")
  // Removed: @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
  public ResponseEntity<?> rescheduleInterview(
      @PathVariable Integer id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {

    try {
      boolean rescheduled = interviewService.rescheduleInterview(id, newDateTime);

      if (rescheduled) {
        return ResponseEntity.ok(MessageResponse.success("Interview rescheduled successfully"));
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(MessageResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<Interview>> getInterviewsByStudentId(@PathVariable Integer studentId) {
    List<Interview> interviews = interviewService.getInterviewsByStudentId(studentId);
    return ResponseEntity.ok(interviews);
  }

  @GetMapping("/experiences")
  public ResponseEntity<List<InterviewExperience>> getAllInterviewExperiences() {
    List<InterviewExperience> experiences = interviewService.getAllInterviewExperiences();
    return ResponseEntity.ok(experiences);
  }


}
