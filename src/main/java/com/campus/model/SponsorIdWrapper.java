package com.campus.model;

// Simple wrapper to capture {"sponsorId": id} structure from JSON arrays
public class SponsorIdWrapper {
    private Integer sponsorId;

    // Getters and Setters
    public Integer getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Integer sponsorId) {
        this.sponsorId = sponsorId;
    }
}
