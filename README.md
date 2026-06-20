# myFSS Devops

Projet DevOps EFREI S8 — binôme Faria / Sylla.

App de suivi des apprentis EFREI, refactorée en monorepo avec 2 services
back, Postgres, Docker, CI et tests.

## Services

- `apprenti-service` (port 8080) : l'app principale, Spring Boot +
  Thymeleaf + JPA/Postgres
- `stats-service` (port 8081) : microservice REST qui calcule des stats
  sur les apprentis

```
[ browser ] --> [ apprenti-service ] --> [ stats-service ]
                       |
                       v
                  [ postgres ]
```

## Lancer

```
docker compose up --build
```

- App : http://localhost:8080 (login `sa` / `password`)
- Stats : http://localhost:8081/api/stats/health

## Tests

```
mvn -f apprenti-service/pom.xml verify
mvn -f stats-service/pom.xml verify
```

Reports JaCoCo dans `*/target/site/jacoco/index.html`,
SpotBugs dans `*/target/spotbugs.html`.

Voir [RAPPORT.md](./RAPPORT.md) pour les détails.
