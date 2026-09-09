package com.fml.common.auth;

public enum UserRole {
    VIEWER,
    TECHNICIAN,
    PLANNER,
    ADMINISTRATOR;

    public int value() {
        return ordinal();
    }

    public static UserRole fromValue(int value) {
        UserRole[] roles = values();
        if (value < 0 || value >= roles.length) {
            throw new IllegalArgumentException("Unknown UserRole value: " + value);
        }
        return roles[value];
    }
}
