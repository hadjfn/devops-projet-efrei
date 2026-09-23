# myFSS — Apprentice tracking & DevOps

[![CI](https://github.com/hadjfn/devops-projet-efrei/actions/workflows/ci.yml/badge.svg)](https://github.com/hadjfn/devops-projet-efrei/actions/workflows/ci.yml)

An EFREI S8 academic project by **Lucas Faria and El Hadj Sylla**, continued here as **El Hadj Sylla’s personal maintenance version**. The original team extended its **myFSS apprentice-tracking application** with a separate statistics service, PostgreSQL persistence, containers and continuous integration. The original authorship and Git history are retained.

The September 2026 personal changes focus on transaction boundaries, HTTP failure handling, regression tests and architectural rules. They are separate from the original coursework; they do not imply that the original project was built solo.

The application lets a tutor create, update, search and delete apprentice records, view related information, and advance the academic year. Its dashboard calls a second service to calculate counts by year and programme.

This repository contains the personal DevOps monorepo. The [collaborative repository](https://github.com/hadjfn/myFSS_Devops) is left unchanged by this work; these are versions of one project, not separate portfolio achievements.

## Architecture

```text
Browser
   |
   v
apprenti-service :8080  ── HTTP/JSON ──>  stats-service :8081
Spring MVC / Thymeleaf                   Stateless REST statistics
   |
   v
Spring Data JPA
   |
   v
PostgreSQL :5432
```

| Component | Implementation |
| --- | --- |
| Application | Java 17, Spring Boot 3.5.6, Thymeleaf, Spring Security |
| Persistence | Spring Data JPA, PostgreSQL 16; H2 in repository tests |
| Statistics | REST endpoint receiving apprentice DTOs and returning aggregate counts |
| Containers | A multi-stage Dockerfile for each service; Docker Compose with PostgreSQL |
| Tests | JUnit 5, Mockito, AssertJ, MockMvc, MockWebServer and ArchUnit |
| CI and quality | GitHub Actions, JaCoCo coverage reports and SpotBugs analysis |

The main service uses controller, service, repository and model packages. Controllers depend on application services, not repositories or the HTTP client. `DashboardService` reads one apprentice dataset and then calls `stats-service` **after the database transaction has ended**. The statistics service separates HTTP validation, calculation logic and DTOs.

A failed or timed-out statistics request is shown as **unavailable**, not as zero apprentices. The list remains usable. Connection and read timeouts are configurable (`STATS_CONNECT_TIMEOUT`, default `2s`; `STATS_READ_TIMEOUT`, default `3s`).

Read the [architecture guide](docs/architecture.md), [decision record](docs/adr/001-application-boundaries.md) and [technical-debt log](docs/technical-debt.md) for the design choices and remaining limits.

## Run locally

### With Docker Compose

Requires Docker with the Compose plugin. From the repository root:

```bash
docker compose up --build
```

- Application: [http://localhost:8080](http://localhost:8080)
- Demo login: `sa` / `password`
- Statistics health endpoint: [http://localhost:8081/api/stats/health](http://localhost:8081/api/stats/health)

PostgreSQL data is stored in the `pgdata` volume. Stop the services with `docker compose down`; this preserves the volume.

### With Java and Maven

Requires **JDK 17**, **Maven 3.9+**, and a PostgreSQL database. You can start just the database using the supplied Compose configuration:

```bash
docker compose up -d postgres
```

Then run each service in a separate terminal:

```bash
mvn -f stats-service/pom.xml spring-boot:run
```

```bash
mvn -f apprenti-service/pom.xml spring-boot:run
```

The default database settings match Docker Compose. To use another local database or statistics service, set `DB_URL`, `DB_USER`, `DB_PASSWORD` and `STATS_SERVICE_URL` before starting `apprenti-service`.

## Tests and continuous integration

Run each service's verification lifecycle from the repository root:

```bash
mvn -f apprenti-service/pom.xml verify
mvn -f stats-service/pom.xml verify
```

The suite covers application services, persistence, MVC endpoints, application contexts and the HTTP client. In particular:

- Archive changes are committed and read through a new persistence context; editing an archived record preserves its state.
- A missing apprentice returns HTTP 404; invalid identifiers return 400; unexpected errors do not expose exception details to the page.
- Deletion requires a POST request and CSRF token. All three dashboard URLs use the same application flow.
- MockWebServer exercises successful requests, server failure, malformed/empty responses, an unreachable server and a read timeout.
- Invalid statistics list entries are rejected before calculation; an empty list remains valid.
- ArchUnit prevents controller-to-repository/client shortcuts, reverse layer dependencies and package cycles.

The existing GET deletion URL now returns 405; the application’s delete button submits a CSRF-protected POST instead. The JSON shape of the statistics endpoint is unchanged.

Reports are generated under each service's `target/` directory:

- Coverage: `target/site/jacoco/index.html`
- Test results: `target/surefire-reports/`
- Static analysis: `target/spotbugs.html` and `target/spotbugsXml.xml`

The [GitHub Actions workflow](.github/workflows/ci.yml) runs on pushes and pull requests to `main`, `develop` and `codex/**` branches. It verifies both services, uploads reports, builds both Docker images and validates the Compose configuration. **It does not publish images or deploy the application.**

## Where to look in the code

- [Apprentice service](apprenti-service/src/main/java/faria/sasikumar/sylla/myfss/service/ApprentiService.java): record operations and academic-year progression.
- [HTTP statistics client](apprenti-service/src/main/java/faria/sasikumar/sylla/myfss/client/StatsClient.java) and [its tests](apprenti-service/src/test/java/faria/sasikumar/sylla/myfss/client/StatsClientTest.java): service-to-service communication and error fallback.
- [Statistics calculation](stats-service/src/main/java/efrei/devops/stats/service/StatsService.java): grouping and aggregation, separate from the REST controller.
- [Repository tests](apprenti-service/src/test/java/faria/sasikumar/sylla/myfss/repository/ApprentiRepositoryTest.java): JPA operations against H2.
- [Docker Compose](docker-compose.yml) and [CI workflow](.github/workflows/ci.yml): local setup and automated checks.

## Scope and next improvements

This is a **local academic demonstration**, not a production deployment. Authentication uses an in-memory demo account, database credentials have local defaults, and Hibernate updates the schema automatically. A production version would need different authentication, secret management and versioned database migrations.

Repository and transaction regression tests currently use H2 rather than PostgreSQL. Adding PostgreSQL integration tests would check database-specific behaviour. This refactor fixes the archive persistence defect and records the remaining work—schema migrations, form DTOs, concurrency, academic-year idempotency and real authentication—in the [technical-debt log](docs/technical-debt.md). It does not claim production readiness or a particular coverage percentage.

## Résumé en français

Projet DevOps EFREI S8 réalisé en binôme **Faria / Sylla**, à partir de notre application myFSS de suivi des apprentis. Cette version poursuit le projet avec un travail personnel de maintenance et de refactorisation : transactions JPA, séparation des responsabilités, gestion des erreurs et tests de non-régression. Deux services Spring Boot, une base PostgreSQL, une interface Thymeleaf, des conteneurs Docker et une intégration continue avec tests et rapports de qualité.

Le [rapport pédagogique en français](RAPPORT.md) décrit le travail remis pour le cours. Ses mesures de couverture correspondent au relevé effectué à cette date ; les rapports d'une nouvelle exécution de `mvn verify` permettent de les actualiser.
