package com.ahk.samples.api;

import com.ahk.samples.domain.SamplePriority;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProcessSampleRequest(
        String externalReference,
        String commodityType,
        String officeLocation,
        SamplePriority priority,
        BigDecimal analysisWeight,
        OffsetDateTime receivedAt,
        String operatorId
) {
}
