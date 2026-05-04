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

## Build And Test

```bash
mvn test
```
