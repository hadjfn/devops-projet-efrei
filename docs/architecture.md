# Architecture of the personal maintenance version

This is an incremental refactor of the Faria / Sylla academic project. The packages, original source history, Thymeleaf screens, database schema and two-service deployment are retained. It is not a rewrite or a production architecture claim.

## Request flow

```text
Browser / Thymeleaf
  -> ApprentiController -> ApprentiService [transaction] -> JPA repositories -> database
  -> DashboardController -> DashboardService
                               -> ApprentiService [read-only transaction]
                               -> StatsClient [bounded HTTP call; no DB transaction]
                                    -> StatsController [validate input]
                                    -> StatsService [aggregate DTOs]
```

`ApprentiService` owns persistence operations and transaction scope. Read methods are read-only; create/update, delete, archive and academic-year progression override that with a write transaction. Archiving therefore updates a managed entity before transaction commit. Updates copy editable fields onto the existing managed record, retaining its archived state; a missing update target is an error instead of an implicit insert.

`DashboardService` reads all apprentices once. Its visible list contains active apprentices; the statistics request includes active and archived records from the same dataset. Network waits do not hold open that read transaction. The dashboard has one controller mapping for `/`, `/dashboard` and `/apprentis/dashboard`.

The detail page originally loaded all companies, visits, missions, evaluations and tutors without rendering them. Those controller dependencies and unused queries have been removed. Their entities and repositories remain because their future relationship to an apprentice requires a separate domain decision.

## Contracts and errors

- A valid `POST /api/stats/summary` still takes a JSON array and returns the original summary shape. Container and element validation reject null records and missing years with 400; an empty dataset returns a valid zero summary.
- `StatsClient` returns `Optional.empty()` for network failure, malformed JSON or an empty HTTP body. The page shows an unavailable message, keeps the apprentice list and does not fabricate counts.
- HTTP connection/read timeouts default to 2/3 seconds and can be overridden with `STATS_CONNECT_TIMEOUT` and `STATS_READ_TIMEOUT`.
- Missing apprentice: 404. Invalid identifier: 400. Unexpected runtime failure: 500 with a generic page message; details stay in server logs.
- Delete is now POST-only with the existing Spring Security CSRF protection. This intentionally closes the mutating GET endpoint.
- Form binding is restricted to editable fields. `archived` is changed through the archive use case, not arbitrary submitted fields.

## Enforced boundaries

The ArchUnit suites run as Maven tests in both services. Main-service controllers cannot depend on repositories or the remote client; services cannot depend on controllers or Spring Web; model/repository code cannot depend on services, controllers or clients; application packages cannot form dependency cycles. Statistics calculation cannot depend on HTTP, and contract DTOs cannot depend on application/controller code.

This remains a small layered Spring application. Persistence entities are still used by MVC forms; they are not a pure domain model. Creating repository ports for every Spring Data method, adding CQRS, or splitting more services would add indirection without a demonstrated requirement. See [ADR 001](adr/001-application-boundaries.md).

## Verification and limits

`mvn -f apprenti-service/pom.xml verify` and `mvn -f stats-service/pom.xml verify` execute the regression and architectural tests, generate JaCoCo reports and run the configured SpotBugs analysis. SpotBugs retains the pre-existing non-blocking configuration; passing Maven is not a claim of zero static-analysis findings.

The archive regression runs **without an enclosing test transaction**: repository setup commits, the service call starts/commits its own transaction, and the assertion reloads the record afterwards. This would catch the original bug that an in-memory unit test missed.

Persistence tests use H2. PostgreSQL-specific behaviour, a live two-service browser workflow, load handling, multi-user authorization and production deployment need further work. The [debt log](technical-debt.md) tracks these explicitly.
