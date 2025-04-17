package com.campus.model;

import com.sun.istack.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for receiving interview scheduling requests.
 */
public class InterviewRequestDTO {

    private Integer jobId;

    private Integer studentId;

    private LocalDateTime dateTime; // Expecting ISO format like "YYYY-MM-DDTHH:mm:ss"

    private Integer recruiterId; // Added recruiterId field

    private String notes;

    // Getters and Setters

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }
}
