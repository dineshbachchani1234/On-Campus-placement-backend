package com.campus.model;

// Simple wrapper to capture {"adminId": id} structure from JSON
public class AdminIdWrapper {
    private Integer adminId;

    // Getters and Setters
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}
