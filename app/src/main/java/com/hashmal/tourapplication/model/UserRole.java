package com.hashmal.tourapplication.model;

public enum UserRole {
    ADMIN("Admin"),
    STAFF("Staff"),
    USER("User");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 