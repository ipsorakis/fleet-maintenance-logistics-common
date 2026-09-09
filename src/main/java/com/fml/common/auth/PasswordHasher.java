package com.fml.common.auth;

/** Salted SHA-256 password hashing used by every FML service. */
public final class PasswordHasher {
    private PasswordHasher() {
    }

    public static String hash(String salt, String password) {
        return Sha256Hex.of(salt + password);
    }
}
