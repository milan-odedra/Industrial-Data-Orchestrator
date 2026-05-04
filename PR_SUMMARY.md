# Pull Request Summary

## Technical Design

This change introduces a high-performance sample tracking backend skeleton for Alfred H Knight's global laboratory network. The design uses PostgreSQL UUID primary keys for globally interoperable sample and audit records, with data integrity enforced both in Java validation and SQL constraints.

Key implementation details:

- Added `lab_samples` and `audit_logs` schema migration under `src/main/resources/db/migration`.
- Added the required `(office_location, status)` composite index for reporting workloads.
- Enforced non-negative `analysis_weight` values with a PostgreSQL check constraint.
- Added immutable Java domain objects for samples and audit events.
- Added repository interfaces to preserve dependency inversion.
- Added Spring JDBC repository adapters for PostgreSQL persistence.
- Added a Spring Boot REST API for sample ingestion via `POST /api/samples`.
- Added Docker Compose PostgreSQL configuration for local runtime verification.
- Added GitHub Actions CI to run Maven tests on pushes and pull requests.
- Added `SampleTrackingService` with constructor injection, validation, transactional persistence, and crisis exception mapping.
- Added `CrisisResolutionException` to document critical persistence defects using stable defect references.

## Test Coverage

JUnit 5 and Mockito tests cover:

- Rejection of negative analysis weights.
- Rejection of missing office location data.
- Successful transactional sample and audit persistence.
- Mockito-based database adapter mapping for sample and audit inserts.
- API controller coverage for successful sample creation and crisis error mapping.
- Prevention of database writes when validation fails.
- Crisis simulation for a database timeout during a high-priority mineral analysis upload.

## Operational Notes

The backend is structured so PostgreSQL persistence, API controllers, and service validation remain independently testable. Docker Compose can be used to run the database locally, while GitHub Actions verifies the Maven test suite automatically.

## Git Commands

```bash
git add .gitignore .gitattributes README.md PR_SUMMARY.md pom.xml src
git commit -m "feat: add global sample tracking backend"
git push origin HEAD
```
