package com.campus.model;

// Simple wrapper to capture {"companyId": id} structure from JSON arrays
public class CompanyIdWrapper {
    private Integer companyId;

    // Getters and Setters
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
