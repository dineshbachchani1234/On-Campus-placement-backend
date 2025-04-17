package com.campus.model;

import java.util.List;

// DTO to represent the incoming request payload for creating/updating events
public class EventRequestDto {

    // Core event fields
    private String title;
    private String description;
    private String date; // Keep as String to match frontend payload format (e.g., "YYYY-MM-DD")
    private String location;

    // Nested objects/lists from the payload
    private AdminIdWrapper admin;
    private List<CompanyInput> companies; // Use CompanyInput
    private List<SponsorInput> sponsors;   // Use SponsorInput

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public AdminIdWrapper getAdmin() {
        return admin;
    }

    public void setAdmin(AdminIdWrapper admin) {
        this.admin = admin;
    }

    public List<CompanyInput> getCompanies() { // Update return type
        return companies;
    }

    public void setCompanies(List<CompanyInput> companies) { // Update parameter type
        this.companies = companies;
    }

    public List<SponsorInput> getSponsors() { // Update return type
        return sponsors;
    }

    public void setSponsors(List<SponsorInput> sponsors) { // Update parameter type
        this.sponsors = sponsors;
    }
}
