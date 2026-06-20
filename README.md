# Projet DevOps EFREI S8

Application de suivi des apprentis EFREI, structurée selon les principes DevOps.

## Architecture

Monorepo composé de deux services backend dockerisés :

- **`apprenti-service`** — Application principale (Spring Boot + Thymeleaf + JPA / PostgreSQL).
  Gère les apprentis, entreprises, missions, visites et évaluations.
  Architecture en couches : **Data (repository)** → **Services** → **Controller (web)**.
- **`stats-service`** — Microservice REST qui calcule des statistiques sur les apprentis.
  Consommé en HTTP par `apprenti-service`.

Une base **PostgreSQL** est lancée par `docker-compose` pour la persistance.

```
┌─────────────────┐    HTTP     ┌─────────────────┐
│ apprenti-service│ ──────────► │  stats-service  │
└────────┬────────┘             └─────────────────┘
         │ JPA
         ▼
   ┌───────────┐
   │ Postgres  │
   └───────────┘
```

## Lancer le projet

```bash
docker compose up --build
```

- UI apprenti : http://localhost:8080
- API stats   : http://localhost:8081/api/stats/...

## Tests & qualité

```bash
# tous les services
mvn -f apprenti-service/pom.xml verify
mvn -f stats-service/pom.xml   verify
```

Rapports générés :
- Couverture : `*/target/site/jacoco/index.html`
- SpotBugs   : `*/target/spotbugs.html`

## Workflow Git

- `main`     : version stable / livrable
- `develop`  : intégration
- `feature/*` : développement, mergées dans `develop` via PR

Voir [`RAPPORT.md`](./RAPPORT.md) pour le rapport complet.
