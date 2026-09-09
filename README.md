# Fml.Common

Cross-cutting building blocks shared by Fleet Maintenance & Logistics (FML) services,
extracted from the [`fleet-maintenance-logistics-source`](https://github.com/ipsorakis/fleet-maintenance-logistics-source)
monolith so that every service can depend on one implementation instead of copying it.

`0.1.0` was the initial release; `0.1.1` parses numeric configuration with the invariant culture.

## What's in the library

| Namespace | Contents |
| --- | --- |
| `FML.Common.Auth` | `User` and `UserRole`, the login/user contracts (`LoginRequest`, `LoginResponse`, `CreateUserRequest`), `PasswordHasher` (salted SHA-256) and `AuthTokenFactory` (opaque session token). |
| `FML.Common.Observability` | `FmlTelemetry`: the `fml-monolith` activity source and meter plus the `fml.telemetry.readings_ingested`, `fml.maintenance.work_orders_created`, `fml.inventory.parts_reordered` and `fml.reporting.reports_generated` counters. |
| `FML.Common.Configuration` | `ConfigurationReader` extensions for fallback-preserving `bool`/`int`/`double`/`decimal` reads off `IConfiguration`, parsed with `CultureInfo.InvariantCulture`. |

The salt and token lifetime are parameters, not configuration the library reads itself —
each service keeps owning its own configuration and passes the values in. Service-specific
logic (persistence, work-order rules, telemetry thresholds) deliberately stays in the
consuming service.

## Using it

The package is published to GitHub Packages. Add the feed once (a personal access token
with `read:packages` is required for local development):

```bash
dotnet nuget add source https://nuget.pkg.github.com/ipsorakis/index.json \
  --name github --username <your-github-username> --password <token> --store-password-in-clear-text
```

Then reference it:

```xml
<PackageReference Include="Fml.Common" Version="0.1.1" />
```

## Versioning

Semantic versioning:

- **patch** — bug fixes with no API change;
- **minor** — backwards-compatible additions;
- **major** — breaking changes to a type, contract or instrument name (instrument and
  metric names are part of the public API because dashboards depend on them).

The version lives in `src/Fml.Common/Fml.Common.csproj`. CI (`.github/workflows/ci.yml`)
builds, format-checks and tests every push and pull request; pushes to `main` publish that
version to GitHub Packages with `--skip-duplicate`, and a `v<version>` tag publishes a
package stamped with the tag's version. Bump the csproj version in the PR that changes the
library, then tag the release.

## Building locally

```bash
dotnet build
dotnet test
dotnet pack src/Fml.Common/Fml.Common.csproj -o artifacts   # produces Fml.Common.0.1.1.nupkg
```
