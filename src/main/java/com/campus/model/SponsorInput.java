package com.campus.model;

// DTO to represent sponsor input, allowing either ID or Name
public class SponsorInput {
    private Integer sponsorId;
    private String name;
    // Add other fields like contactInfo, website if needed for creation

    // Getters and Setters
    public Integer getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Integer sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Optional: Add constructor or other methods as needed
}
