package com.fml.common.auth;

import java.time.Instant;

public record LoginResponse(String token, String username, UserRole role, Instant expiresUtc) {
}
