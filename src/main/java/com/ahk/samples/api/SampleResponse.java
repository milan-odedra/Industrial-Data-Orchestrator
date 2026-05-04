package com.ahk.samples.api;

import com.ahk.samples.domain.LabSample;
import com.ahk.samples.domain.SamplePriority;
import com.ahk.samples.domain.SampleStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SampleResponse(
        UUID sampleId,
        String externalReference,
        String commodityType,
        String officeLocation,
        SampleStatus status,
        SamplePriority priority,
        BigDecimal analysisWeight,
        OffsetDateTime receivedAt
) {
    public static SampleResponse from(LabSample sample) {
        return new SampleResponse(
                sample.sampleId(),
                sample.externalReference(),
                sample.commodityType(),
                sample.officeLocation(),
                sample.status(),
                sample.priority(),
                sample.analysisWeight(),
                sample.receivedAt()
        );
    }
}
