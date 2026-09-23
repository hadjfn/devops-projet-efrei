# myFSS — Apprentice tracking & DevOps

[![CI](https://github.com/hadjfn/devops-projet-efrei/actions/workflows/ci.yml/badge.svg)](https://github.com/hadjfn/devops-projet-efrei/actions/workflows/ci.yml)

An EFREI S8 academic project by **Lucas Faria and El Hadj Sylla**. We extended our existing **myFSS apprentice-tracking application** with a separate statistics service, PostgreSQL persistence, containers and continuous integration.

The application lets a tutor create, update, search and delete apprentice records, view related information, and advance the academic year. Its dashboard calls a second service to calculate counts by year and programme.

This repository contains the DevOps monorepo. The [collaborative repository](https://github.com/hadjfn/myFSS_Devops) contains the same application and later documentation and CI changes; these are two versions of one project.

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
| Tests | JUnit 5, Mockito, AssertJ, MockMvc and MockWebServer |
| CI and quality | GitHub Actions, JaCoCo coverage reports and SpotBugs analysis |

The main service uses controller, service, repository and model packages. The statistics service separates its controller, calculation logic and DTOs. If the statistics HTTP request fails, the client returns an empty summary so the dashboard can still render.

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

The test suite contains **33 test methods** across services, repositories, controllers, application-context checks and the HTTP client. MockWebServer tests cover the statistics request and response, a server error and an unreachable service.

Reports are generated under each service's `target/` directory:

- Coverage: `target/site/jacoco/index.html`
- Test results: `target/surefire-reports/`
- Static analysis: `target/spotbugs.html` and `target/spotbugsXml.xml`

The [GitHub Actions workflow](.github/workflows/ci.yml) runs on pushes and pull requests to `main` and `develop`. It verifies both services, uploads reports, builds both Docker images and validates the Compose configuration. **It does not publish images or deploy the application.**

## Where to look in the code

- [Apprentice service](apprenti-service/src/main/java/faria/sasikumar/sylla/myfss/service/ApprentiService.java): record operations and academic-year progression.
- [HTTP statistics client](apprenti-service/src/main/java/faria/sasikumar/sylla/myfss/client/StatsClient.java) and [its tests](apprenti-service/src/test/java/faria/sasikumar/sylla/myfss/client/StatsClientTest.java): service-to-service communication and error fallback.
- [Statistics calculation](stats-service/src/main/java/efrei/devops/stats/service/StatsService.java): grouping and aggregation, separate from the REST controller.
- [Repository tests](apprenti-service/src/test/java/faria/sasikumar/sylla/myfss/repository/ApprentiRepositoryTest.java): JPA operations against H2.
- [Docker Compose](docker-compose.yml) and [CI workflow](.github/workflows/ci.yml): local setup and automated checks.

## Scope and next improvements

This is a **local academic demonstration**, not a production deployment. Authentication uses an in-memory demo account, database credentials have local defaults, and Hibernate updates the schema automatically. A production version would need different authentication, secret management and versioned database migrations.

Repository tests currently use H2 rather than PostgreSQL. Adding PostgreSQL integration tests would check database-specific behaviour. The manual archive operation also needs a persistence regression test; its current unit test only checks the in-memory entity.

## Résumé en français

Projet DevOps EFREI S8 réalisé en binôme **Faria / Sylla**, à partir de notre application myFSS de suivi des apprentis. Deux services Spring Boot, une base PostgreSQL, une interface Thymeleaf, des conteneurs Docker et une intégration continue avec tests et rapports de qualité.

Le [rapport pédagogique en français](RAPPORT.md) décrit le travail remis pour le cours. Ses mesures de couverture correspondent au relevé effectué à cette date ; les rapports d'une nouvelle exécution de `mvn verify` permettent de les actualiser.
