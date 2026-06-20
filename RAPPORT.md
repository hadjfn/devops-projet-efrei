# Rapport Projet DevOps

EFREI S8 — binôme Faria / Sylla
Dépôt : https://github.com/hadjfn/myFSS_Devops

## Sujet

On a repris notre appli **myFSS** (suivi des apprentis EFREI) et on l'a
fait évoluer pour respecter les règles DevOps du cours : architecture
en couches, deux services back dockerisés, tests partout, CI.

L'app principale (`apprenti-service`) gère les apprentis, leurs
entreprises, missions, visites, évaluations et maîtres d'apprentissage.

On a ajouté un deuxième service (`stats-service`) qui calcule des stats
(total apprentis, archivés, par année, par programme, moyenne d'année)
à partir d'un JSON envoyé par l'app principale. C'est juste un petit
microservice REST stateless.

## Archi

Architecture en couches respectée dans les 2 services :

**apprenti-service** (port 8080)
- `controller/` : les controllers web Thymeleaf (Dashboard, Apprenti, Login)
- `service/` : `ApprentiService` (logique métier)
- `repository/` : 6 repos JPA
- `model/` : les entités JPA
- + `client/` : le client HTTP vers stats-service

**stats-service** (port 8081)
- `controller/StatsController` : endpoints REST
- `service/StatsService` : calcule les stats
- `data/` : DTO (records Java)

Le flow du dashboard :
1. user se connecte → `/dashboard`
2. `DashboardController` récupère la liste des apprentis via JPA
3. il envoie cette liste à stats-service via HTTP POST
4. stats-service renvoie les stats, on les affiche dans la page

Schéma :

```
[ navigateur ] -- HTTP --> [ apprenti-service :8080 ] -- HTTP --> [ stats-service :8081 ]
                                       |
                                      JPA
                                       v
                                [ postgres :5432 ]
```

## Git

Branches :
- `main` : version stable
- `develop` : intégration
- `feature/...` : pour les grosses features

On a utilisé des merges `--no-ff` pour garder l'historique lisible, et
quelques fix directement sur develop quand c'était petit.

## CI

GitHub Actions, fichier `.github/workflows/ci.yml`. Sur chaque push
et chaque PR vers main/develop, ça lance :
- Build + tests + JaCoCo + SpotBugs sur les 2 services (en parallèle)
- Build des images Docker des 2 services
- Validation du docker-compose

Tous les rapports sont uploadés comme artifacts (coverage, spotbugs,
tests). Pipeline verte sur main.

## Tests

On a utilisé **JUnit 5** + **Mockito** + **AssertJ**.

Pour les couches :
- Unit (services) : Mockito sur les repos
- Data (repos) : `@DataJpaTest` avec H2 en mémoire
- Controllers : `@WebMvcTest` + MockMvc + Spring Security Test
- Communication HTTP entre les services : **MockWebServer** (OkHttp) pour
  mocker stats-service côté apprenti-service

Au total : 33 tests.

| Service | Classe | Couche | Tests |
|---|---|---|---|
| apprenti | ApprentiServiceTest | service | 9 |
| apprenti | ApprentiRepositoryTest | data | 4 |
| apprenti | ApprentiControllerTest | controller | 6 |
| apprenti | DashboardControllerTest | controller | 2 |
| apprenti | StatsClientTest | mock web | 3 |
| apprenti | MyFssApplicationTests | smoke | 1 |
| stats | StatsServiceTest | service | 5 |
| stats | StatsControllerTest | controller | 2 |
| stats | StatsServiceApplicationTests | smoke | 1 |

## Couverture (JaCoCo)

- apprenti-service : **88,0 %** d'instructions (504/573)
- stats-service : **97,1 %** d'instructions (166/171)

Rapports générés dans `target/site/jacoco/index.html` après `mvn verify`,
et dispo en artifacts sur GitHub Actions.

## Qualité

SpotBugs en `Max effort`, seuil `Medium`, exécuté à la phase verify.
On a ajouté un filtre pour ignorer `EI_EXPOSE_REP` qui remonte sur les
records Java (faux positifs).

Résultat : **0 bug détecté** sur les 2 services.

## Docker

- 2 Dockerfiles multi-stage (build maven + run JRE 17) → images légères
- docker-compose : postgres 16 + apprenti-service + stats-service
- healthcheck sur postgres, apprenti-service attend que ce soit prêt
- variables d'env pour les URLs / credentials

Pour lancer tout :

```
docker compose up --build
```

- App : http://localhost:8080 (login : `sa` / `password`)
- Stats : http://localhost:8081/api/stats/health

## Récap exigences

| Exigence | OK | Où |
|---|:-:|---|
| Dépôt Git | ✓ | GitHub myFSS_Devops |
| Pipeline CI | ✓ | `.github/workflows/ci.yml` |
| Archi Data/Services/Controller | ✓ | cf §archi |
| 2 services back + Docker | ✓ | apprenti + stats + compose |
| Tests toutes couches | ✓ | cf §tests |
| Tests unitaires | ✓ | JUnit 5 + Mockito |
| Mocks web | ✓ | MockWebServer dans StatsClientTest |
| Couverture | ✓ | 88% et 97% |
| Qualité | ✓ | SpotBugs, 0 bug |
| Bonus front | ✓ | Thymeleaf + CSS |
| Bonus BDD | ✓ | PostgreSQL via Docker |

## Google Labs

*(à compléter avec les captures)*
