# Industrial Data Orchestrator

## Global Sample Tracking Architecture

The sample tracking service provides a high-integrity backend foundation for Alfred H Knight laboratory sample data across metals, minerals, and agriculture operations. It uses UUID primary keys so records can move safely across globally distributed offices, laboratories, and time zones without depending on local database sequences.

The persistence model is intentionally compact:

- `lab_samples` stores the authoritative sample record, including commodity, office location, status, priority, analysis weight, and timezone-aware receipt timestamps.
- `audit_logs` records append-only operational events for traceability, defect investigation, and compliance review.
- PostgreSQL constraints reject invalid status, priority, and negative `analysis_weight` values at the database boundary.
- A composite index on `(office_location, status)` supports fast regional reporting for global trade and laboratory operations teams.

The Java service follows a ports-and-services shape. `SampleTrackingService` owns validation and transaction orchestration, while `LabSampleRepository` and `AuditLogRepository` keep database persistence behind interfaces. Spring JDBC adapters provide the PostgreSQL insert path behind those ports. Constructor injection makes dependencies explicit and easy to replace in tests or production configuration. Critical persistence failures are wrapped in `CrisisResolutionException` with a defect reference so operational incidents can be documented consistently.

This design meets the continuing needs of the global Alfred H Knight group by emphasizing globally unique identifiers, timezone-aware records, auditable state changes, and focused validation before high-value mineral, metal, or agriculture sample data is committed.

## API

The Spring Boot API accepts sample uploads at `POST /api/samples`.

Example request:

```bash
curl -X POST http://localhost:8080/api/samples \
  -H "Content-Type: application/json" \
  -d '{
    "externalReference": "AHK-MIN-001",
    "commodityType": "Copper Concentrate",
    "officeLocation": "Liverpool, UK",
    "priority": "HIGH",
    "analysisWeight": 12.5000,
    "receivedAt": "2026-05-04T09:30:00Z",
    "operatorId": "operator.liverpool"
  }'
```

Validation failures return `400 Bad Request`. Critical persistence failures return `503 Service Unavailable` with a stable crisis defect reference.

## Build And Test

```bash
mvn test
```

## Run Locally

Start PostgreSQL:

```bash
docker compose up -d
```

Run the API:

```bash
mvn spring-boot:run
```

The application uses these defaults:

- Database URL: `jdbc:postgresql://localhost:5432/ahk_samples`
- Username: `ahk`
- Password: `ahk`

GitHub Actions runs `mvn test` on pushes and pull requests to `main`.
