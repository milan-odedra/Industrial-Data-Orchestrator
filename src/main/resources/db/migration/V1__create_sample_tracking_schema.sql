CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE lab_samples (
    sample_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_reference VARCHAR(128) NOT NULL UNIQUE,
    commodity_type VARCHAR(64) NOT NULL,
    office_location VARCHAR(96) NOT NULL,
    status VARCHAR(32) NOT NULL,
    priority VARCHAR(32) NOT NULL,
    analysis_weight NUMERIC(12, 4) NOT NULL,
    received_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_lab_samples_analysis_weight_non_negative CHECK (analysis_weight >= 0),
    CONSTRAINT ck_lab_samples_status CHECK (status IN ('RECEIVED', 'VALIDATED', 'IN_ANALYSIS', 'REJECTED', 'COMPLETED')),
    CONSTRAINT ck_lab_samples_priority CHECK (priority IN ('STANDARD', 'HIGH', 'CRITICAL'))
);

CREATE INDEX idx_lab_samples_office_location_status
    ON lab_samples (office_location, status);

CREATE TABLE audit_logs (
    audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sample_id UUID NOT NULL REFERENCES lab_samples(sample_id) ON DELETE CASCADE,
    event_type VARCHAR(64) NOT NULL,
    event_payload JSONB NOT NULL,
    created_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_logs_sample_id_created_at
    ON audit_logs (sample_id, created_at DESC);
