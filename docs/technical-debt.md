# Technical-debt log

Scope: El Hadj Sylla’s personal maintenance version of the Faria / Sylla academic project. Priorities are relative to a local demonstration, not a production certification.

## Addressed in September 2026

| Debt / defect | Change | Evidence |
| --- | --- | --- |
| Archive only modified an in-memory entity | Service write transaction; reload after commit | `ApprentiPersistenceTest.archive_isCommittedAndVisibleInANewPersistenceContext` |
| Editing could reset an archive flag | Copy only editable fields onto a managed record | Persistence regressions for archived edits and new records |
| Controller loaded five unused repositories | Removed those dependencies and queries | Detail view test plus controller boundary rule |
| Dashboard routes returned inconsistent models | One controller and orchestration service | Parameterized test of all three URLs |
| Remote failure displayed false zero counts | Explicit absence, page message and bounded timeouts | HTTP client failure/timeout and MVC view tests |
| Missing records returned generic failures | Typed not-found exception and HTTP status mapping | 404/400/500 controller tests |
| Deletion used GET | POST form with CSRF | Rejected GET and missing-CSRF tests |
| Layer rules existed only as convention | ArchUnit rules in both services | Maven test lifecycle |

## Remaining work

| Priority | Item | Why it remains / next acceptance evidence |
| --- | --- | --- |
| High before real deployment | Replace demo authentication and local default credentials | Still a local demo. Introduce real identities/roles and secrets; test unauthorized access and record ownership. |
| High before sharing real data | PostgreSQL migration and integration tests | Hibernate `ddl-auto=update` remains; H2 cannot prove PostgreSQL compatibility. Add versioned migrations and a disposable PostgreSQL test with upgrade/rollback checks. |
| Medium | Dedicated form/request DTOs and year policy | Entities still carry MVC validation. Extract an input model once constraints for programme/year are agreed; keep archive state server-owned. |
| Medium | Idempotent academic-year progression | Repeated requests currently advance the year again, including archived records. Model the academic period and duplicate-request policy; test repeated and concurrent submissions before changing existing semantics. |
| Medium | Optimistic locking and pagination | Concurrent edits can overwrite fields; dashboards load all records. Add `@Version`, conflict handling and paged queries when usage requires them. |
| Medium | Contract/data minimization | Statistics DTOs still include names and other fields the calculation does not need. Agree a versioned contract removing unnecessary personal data and add consumer/provider tests. |
| Medium | More actionable static analysis | Existing broad exposure exclusions and non-blocking SpotBugs remain. Review each finding and narrow suppressions before turning findings into a CI gate. |
| Low | Apprentice-related domain entities | Company/visit/mission/etc. repositories are retained but their relationships and screens are incomplete. Define real use cases before wiring them into the detail page. |
| Low | End-to-end deployment smoke test | Maven verifies components, not the deployed Compose topology. Add a login/create/archive/stats-outage smoke test against disposable services. |

## Keeping this list useful

A new abstraction should resolve a named problem here or a tested requirement. Each resolved item needs a linked change and evidence; passing tests do not erase unresolved design choices. Revisit priorities when the application moves beyond a local portfolio demonstration.
