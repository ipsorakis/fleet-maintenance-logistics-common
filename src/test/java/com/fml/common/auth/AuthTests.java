package com.fml.common.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class AuthTests {

    @Test
    void hashingIsDeterministicAndNeverReturnsThePassword() {
        String hash = PasswordHasher.hash("salt", "s3cret");

        assertThat(hash).isEqualTo(PasswordHasher.hash("salt", "s3cret"));
        assertThat(hash).isNotEqualTo("s3cret");
        assertThat(hash).hasSize(64);
    }

    @Test
    void hashingIsSalted() {
        assertThat(PasswordHasher.hash("salt-a", "s3cret"))
                .isNotEqualTo(PasswordHasher.hash("salt-b", "s3cret"));
    }

    @Test
    void hashesMatchTheDotNetImplementation() {
        assertThat(PasswordHasher.hash("salt", "s3cret"))
                .isEqualTo("2D03BC30A3C7B88194D8A8CF613E2B7B0CE5D07F5E03836545684A8A21EAE47A");
        assertThat(PasswordHasher.hash("fml-salt", "P\u00e4ssw0rd!"))
                .isEqualTo("5C69188AB4EF8036483A8DF7A1998713F2229124F761689FB4125B982E2EBB04");
        assertThat(PasswordHasher.hash("", ""))
                .isEqualTo("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855");
    }

    @Test
    void tokensDifferPerUserAndPerExpiry() {
        Instant expires = Instant.parse("2026-01-01T00:00:00Z");

        String token = AuthTokenFactory.issue("salt", 1, "planner", expires);

        assertThat(token).isEqualTo(AuthTokenFactory.issue("salt", 1, "planner", expires));
        assertThat(token).isNotEqualTo(AuthTokenFactory.issue("salt", 2, "planner", expires));
        assertThat(token).isNotEqualTo(
                AuthTokenFactory.issue("salt", 1, "planner", expires.plusSeconds(60)));
        assertThat(token).isNotEqualTo(AuthTokenFactory.issue("other-salt", 1, "planner", expires));
    }

    @Test
    void roundTripFormatMatchesTheDotNetSpecifier() {
        assertThat(AuthTokenFactory.ROUND_TRIP_FORMATTER.format(Instant.parse("2026-01-01T00:00:00Z")))
                .isEqualTo("2026-01-01T00:00:00.0000000Z");
        assertThat(AuthTokenFactory.ROUND_TRIP_FORMATTER.format(Instant.parse("2026-09-09T12:34:56.789Z")))
                .isEqualTo("2026-09-09T12:34:56.7890000Z");
    }

    @Test
    void tokensMatchTheDotNetImplementation() {
        assertThat(AuthTokenFactory.issue("salt", 1, "planner", Instant.parse("2026-01-01T00:00:00Z")))
                .isEqualTo("9C1D0C4BC5C99DF959C283B4A61E65225E67E06A50195C82F991EC27D4DD86B0");
        assertThat(AuthTokenFactory.issue("other-salt", 42, "tech", Instant.parse("2026-09-09T12:34:56.789Z")))
                .isEqualTo("9A0BE7506B16B5B457B0F6F687E732880A01C2490EDD4FDE7B1C068190940AB7");
    }

    @Test
    void userDefaultsToAnActiveViewer() {
        User user = new User();

        assertThat(user.isActive()).isTrue();
        assertThat(user.getRole()).isEqualTo(UserRole.VIEWER);
        assertThat(user.getLastLoginUtc()).isNull();
        assertThat(user.getCreatedUtc()).isNotNull();
    }

    @Test
    void userRoleOrdinalsAreStable() {
        assertThat(UserRole.VIEWER.value()).isZero();
        assertThat(UserRole.TECHNICIAN.value()).isEqualTo(1);
        assertThat(UserRole.PLANNER.value()).isEqualTo(2);
        assertThat(UserRole.ADMINISTRATOR.value()).isEqualTo(3);
        assertThat(UserRole.fromValue(2)).isEqualTo(UserRole.PLANNER);
    }

    @Test
    void contractsCarryTheirValues() {
        LoginRequest login = new LoginRequest("planner", "s3cret");
        Instant expires = Instant.parse("2026-01-01T00:00:00Z");
        LoginResponse response = new LoginResponse("token", "planner", UserRole.PLANNER, expires);
        CreateUserRequest create =
                new CreateUserRequest("planner", "planner@fml.example", "s3cret", UserRole.PLANNER);

        assertThat(login.username()).isEqualTo("planner");
        assertThat(login.password()).isEqualTo("s3cret");
        assertThat(response.token()).isEqualTo("token");
        assertThat(response.role()).isEqualTo(UserRole.PLANNER);
        assertThat(response.expiresUtc()).isEqualTo(expires);
        assertThat(create.email()).isEqualTo("planner@fml.example");
    }
}
