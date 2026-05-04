package com.ahk.samples.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ahk.samples.domain.LabSample;
import com.ahk.samples.domain.SamplePriority;
import com.ahk.samples.service.CrisisResolutionException;
import com.ahk.samples.service.SampleTrackingService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SampleControllerTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-04T10:15:30Z"), ZoneOffset.UTC);

    @Test
    void processSampleReturnsCreatedSample() throws Exception {
        SampleTrackingService service = org.mockito.Mockito.mock(SampleTrackingService.class);
        UUID sampleId = UUID.fromString("7eb1dc7d-2e89-49de-9f8b-2268861361d1");
        when(service.processSample(any(LabSample.class), eq("operator.liverpool")))
                .thenReturn(validPersistedSample(sampleId));
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(post("/api/samples")
                        .contentType("application/json")
                        .content("""
                                {
                                  "externalReference": "AHK-MIN-001",
                                  "commodityType": "Copper Concentrate",
                                  "officeLocation": "Liverpool, UK",
                                  "priority": "HIGH",
                                  "analysisWeight": 12.5000,
                                  "receivedAt": "2026-05-04T09:30:00Z",
                                  "operatorId": "operator.liverpool"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/samples/" + sampleId))
                .andExpect(jsonPath("$.sampleId").value(sampleId.toString()))
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void crisisExceptionReturnsServiceUnavailableWithDefectReference() throws Exception {
        SampleTrackingService service = org.mockito.Mockito.mock(SampleTrackingService.class);
        when(service.processSample(any(LabSample.class), eq("operator.liverpool")))
                .thenThrow(new CrisisResolutionException(
                        "Critical database timeout while processing high-value sample upload.",
                        "CRISIS-DB-TIMEOUT",
                        new RuntimeException("timeout")
                ));
        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(post("/api/samples")
                        .contentType("application/json")
                        .content("""
                                {
                                  "externalReference": "AHK-MIN-001",
                                  "commodityType": "Copper Concentrate",
                                  "officeLocation": "Liverpool, UK",
                                  "priority": "HIGH",
                                  "analysisWeight": 12.5000,
                                  "receivedAt": "2026-05-04T09:30:00Z",
                                  "operatorId": "operator.liverpool"
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("CRISIS-DB-TIMEOUT"));
    }

    private static MockMvc mockMvc(SampleTrackingService service) {
        return MockMvcBuilders
                .standaloneSetup(new SampleController(service))
                .setControllerAdvice(new ApiExceptionHandler(CLOCK))
                .build();
    }

    private static LabSample validPersistedSample(UUID sampleId) {
        return LabSample.received(
                "AHK-MIN-001",
                "Copper Concentrate",
                "Liverpool, UK",
                SamplePriority.HIGH,
                new BigDecimal("12.5000"),
                OffsetDateTime.parse("2026-05-04T09:30:00Z")
        ).withSampleId(sampleId);
    }
}
