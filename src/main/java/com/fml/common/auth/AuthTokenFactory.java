package com.fml.common.auth;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/** Issues the opaque session token returned by an FML login. */
public final class AuthTokenFactory {
    /**
     * Reproduces .NET's round-trip ("O") format for UTC instants: an ISO 8601 timestamp with
     * exactly seven fractional-second digits and a trailing {@code Z}.
     */
    public static final DateTimeFormatter ROUND_TRIP_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd'T'HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 7, 7, true)
            .appendLiteral('Z')
            .toFormatter()
            .withZone(ZoneOffset.UTC);

    private AuthTokenFactory() {
    }

    public static String issue(String salt, int userId, String username, Instant expiresUtc) {
        return Sha256Hex.of(userId + ":" + username + ":" + ROUND_TRIP_FORMATTER.format(expiresUtc) + ":" + salt);
    }
}
