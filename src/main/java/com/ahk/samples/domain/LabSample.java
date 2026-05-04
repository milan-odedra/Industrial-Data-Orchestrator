package com.ahk.samples.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class LabSample {
    private final UUID sampleId;
    private final String externalReference;
    private final String commodityType;
    private final String officeLocation;
    private final SampleStatus status;
    private final SamplePriority priority;
    private final BigDecimal analysisWeight;
    private final OffsetDateTime receivedAt;

    public LabSample(
            UUID sampleId,
            String externalReference,
            String commodityType,
            String officeLocation,
            SampleStatus status,
            SamplePriority priority,
            BigDecimal analysisWeight,
            OffsetDateTime receivedAt
    ) {
        this.sampleId = sampleId;
        this.externalReference = externalReference;
        this.commodityType = commodityType;
        this.officeLocation = officeLocation;
        this.status = status;
        this.priority = priority;
        this.analysisWeight = analysisWeight;
        this.receivedAt = receivedAt;
    }

    public static LabSample received(
            String externalReference,
            String commodityType,
            String officeLocation,
            SamplePriority priority,
            BigDecimal analysisWeight,
            OffsetDateTime receivedAt
    ) {
        return new LabSample(
                null,
                externalReference,
                commodityType,
                officeLocation,
                SampleStatus.RECEIVED,
                priority,
                analysisWeight,
                receivedAt
        );
    }

    public LabSample withSampleId(UUID persistedSampleId) {
        return new LabSample(
                Objects.requireNonNull(persistedSampleId, "persistedSampleId must not be null"),
                externalReference,
                commodityType,
                officeLocation,
                status,
                priority,
                analysisWeight,
                receivedAt
        );
    }

    public UUID sampleId() {
        return sampleId;
    }

    public String externalReference() {
        return externalReference;
    }

    public String commodityType() {
        return commodityType;
    }

    public String officeLocation() {
        return officeLocation;
    }

    public SampleStatus status() {
        return status;
    }

    public SamplePriority priority() {
        return priority;
    }

    public BigDecimal analysisWeight() {
        return analysisWeight;
    }

    public OffsetDateTime receivedAt() {
        return receivedAt;
    }
}
