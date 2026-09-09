package com.fml.common.auth;

import java.time.Instant;

public class User {
    private int id;
    private String username = "";
    private String email = "";
    private String passwordHash = "";
    private UserRole role = UserRole.VIEWER;
    private boolean active = true;
    private Instant createdUtc = Instant.now();
    private Instant lastLoginUtc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(Instant createdUtc) {
        this.createdUtc = createdUtc;
    }

    public Instant getLastLoginUtc() {
        return lastLoginUtc;
    }

    public void setLastLoginUtc(Instant lastLoginUtc) {
        this.lastLoginUtc = lastLoginUtc;
    }
}
