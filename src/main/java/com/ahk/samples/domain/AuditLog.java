package com.ahk.samples.domain;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class AuditLog {
    private final UUID auditId;
    private final UUID sampleId;
    private final String eventType;
    private final String eventPayload;
    private final String createdBy;
    private final OffsetDateTime createdAt;

    public AuditLog(UUID auditId, UUID sampleId, String eventType, String eventPayload, String createdBy, OffsetDateTime createdAt) {
        this.auditId = auditId;
        this.sampleId = Objects.requireNonNull(sampleId, "sampleId must not be null");
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.eventPayload = Objects.requireNonNull(eventPayload, "eventPayload must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static AuditLog sampleReceived(LabSample sample, String createdBy, OffsetDateTime createdAt) {
        String payload = """
                {"externalReference":"%s","officeLocation":"%s","status":"%s","priority":"%s"}"""
                .formatted(
                        escapeJson(sample.externalReference()),
                        escapeJson(sample.officeLocation()),
                        sample.status(),
                        sample.priority()
                );
        return new AuditLog(null, sample.sampleId(), "SAMPLE_RECEIVED", payload, createdBy, createdAt);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public UUID auditId() {
        return auditId;
    }

    public UUID sampleId() {
        return sampleId;
    }

    public String eventType() {
        return eventType;
    }

    public String eventPayload() {
        return eventPayload;
    }

    public String createdBy() {
        return createdBy;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }
}
