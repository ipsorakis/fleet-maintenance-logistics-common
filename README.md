# Fml.Common

Cross-cutting building blocks shared by Fleet Maintenance & Logistics (FML) services,
extracted from the [`fleet-maintenance-logistics-source`](https://github.com/ipsorakis/fleet-maintenance-logistics-source)
monolith so that every service can depend on one implementation instead of copying it.

`0.1.0` was the initial release; `0.1.1` parses numeric configuration with the invariant culture.
The library is a Java 17 / Spring Boot artifact built with Maven; it replaces the original .NET 8
implementation and keeps its observable behaviour, including byte-compatible password hashes and
session tokens and identical telemetry instrument names.

## What's in the library

| Package | Contents |
| --- | --- |
| `com.fml.common.auth` | `User` and `UserRole`, the login/user contracts (`LoginRequest`, `LoginResponse`, `CreateUserRequest`), `PasswordHasher` (salted SHA-256, uppercase hex) and `AuthTokenFactory` (opaque session token; expiry rendered in .NET's round-trip format with seven fractional-second digits). |
| `com.fml.common.observability` | `FmlTelemetry`: the `fml-monolith` tracer and meter plus the `fml.telemetry.readings_ingested`, `fml.maintenance.work_orders_created`, `fml.inventory.parts_reordered` and `fml.reporting.reports_generated` counters. |
| `com.fml.common.configuration` | `ConfigurationReader` static helpers for fallback-preserving `boolean`/`int`/`double`/`BigDecimal` reads off Spring's `Environment`, parsed locale-independently. |

The salt and token lifetime are parameters, not configuration the library reads itself —
each service keeps owning its own configuration and passes the values in. Service-specific
logic (persistence, work-order rules, telemetry thresholds) deliberately stays in the
consuming service.

## Using it

The artifact is published to GitHub Packages. Add the repository and credentials once (a personal
access token with `read:packages` is required for local development) — in `~/.m2/settings.xml`:

```xml
<servers>
  <server>
    <id>github</id>
    <username>your-github-username</username>
    <password>your-token</password>
  </server>
</servers>
```

and in the consuming project's `pom.xml`:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/ipsorakis/fleet-maintenance-logistics-common</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.fml</groupId>
  <artifactId>fml-common</artifactId>
  <version>0.1.1</version>
</dependency>
```

## Versioning

Semantic versioning:

- **patch** — bug fixes with no API change;
- **minor** — backwards-compatible additions;
- **major** — breaking changes to a type, contract or instrument name (instrument and
  metric names are part of the public API because dashboards depend on them).

The version lives in `pom.xml`. CI (`.github/workflows/ci.yml`) builds and tests every push
and pull request; pushes to `main` publish that version to GitHub Packages, and a
`v<version>` tag publishes an artifact stamped with the tag's version. Bump the `pom.xml`
version in the PR that changes the library, then tag the release.

## Building locally

```bash
mvn verify
mvn package        # produces target/fml-common-0.1.1.jar plus sources and javadoc jars
mvn deploy         # publishes to GitHub Packages (needs the `github` server credentials)
```
