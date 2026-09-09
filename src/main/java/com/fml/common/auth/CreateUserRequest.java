package com.fml.common.auth;

public record CreateUserRequest(String username, String email, String password, UserRole role) {
}
