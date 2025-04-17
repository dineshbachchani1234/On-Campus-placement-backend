package com.campus.model;

// DTO to represent company input, allowing either ID or Name
public class CompanyInput {
    private Integer companyId;
    private String name;
    // Add other fields like website, description if needed for creation

    // Getters and Setters
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Optional: Add constructor or other methods as needed
}
