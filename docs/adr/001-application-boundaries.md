# ADR 001 — Keep a layered application and make boundaries executable

- Status: accepted for this personal version
- Date: 2026-09-23
- Origin: Faria / Sylla academic myFSS project

## Problem

The original controller injected five repositories whose results were unused by its detail template. Two dashboard handlers produced different models. Archiving only changed a Java object outside a service transaction. The HTTP client represented an outage as zero records. These are concrete ownership and correctness problems, not reasons to introduce more microservices.

## Decision

Keep the two existing deployable services. Put persistence transactions in `ApprentiService`; place dashboard orchestration in `DashboardService`, with its HTTP call outside the read transaction. Keep the remote statistics adapter in `client`. Do not introduce interfaces that have one in-process implementation and no substitution need. Enforce the package rules using ArchUnit.

Represent an unavailable remote summary explicitly with `Optional`, and show its absence in the view. Keep the successful JSON contract unchanged. Validate list elements at the HTTP boundary. Restrict record deletion to POST with CSRF.

## Consequences

Controllers have fewer dependencies and can be tested using application-service contracts. Transaction regressions reload committed records. Network failure is distinguishable from a legitimate empty dataset. The HTTP deletion method deliberately changes, while the UI retains its delete action.

The design is still coupled to Spring/JPA and uses entities in forms. Those compromises are visible and tracked; a separate domain model or a ports-and-adapters rewrite would need a larger domain or integration requirement. Additional layers are not evidence of better architecture by themselves.

## Evidence

- `ApprentiPersistenceTest`: committed archive, preserved archive state, unknown update target, academic-year persistence.
- `DashboardControllerTest`: all dashboard aliases and unavailable-statistics display.
- `StatsClientTest`: request/response contract and failure/timeout behaviour.
- `ArchitectureTest` in both services: dependency rules.
- `StatsControllerTest`: invalid collection elements rejected before calculation.
