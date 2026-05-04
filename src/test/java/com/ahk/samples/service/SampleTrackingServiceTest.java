package com.ahk.samples.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ahk.samples.domain.LabSample;
import com.ahk.samples.domain.SamplePriority;
import com.ahk.samples.repository.AuditLogRepository;
import com.ahk.samples.repository.LabSampleRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class SampleTrackingServiceTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-04T10:15:30Z"), ZoneOffset.UTC);

    @Mock
    private LabSampleRepository labSampleRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    private SampleTrackingService service;

    @BeforeEach
    void setUp() {
        service = new SampleTrackingService(
                labSampleRepository,
                auditLogRepository,
                new TransactionTemplate(new StubPlatformTransactionManager()),
                CLOCK
        );
    }

    @Test
    void validationRejectsNegativeAnalysisWeight() {
        LabSample sample = validSample("-0.001");

        assertThatThrownBy(() -> service.validate(sample, "operator.liverpool"))
                .isInstanceOf(SampleValidationException.class)
                .hasMessage("Analysis weight cannot be negative.");
    }

    @Test
    void validationRejectsMissingOfficeLocation() {
        LabSample sample = LabSample.received(
                "AHK-MIN-001",
                "Copper Concentrate",
                " ",
                SamplePriority.HIGH,
                new BigDecimal("12.5000"),
                OffsetDateTime.now(CLOCK)
        );

        assertThatThrownBy(() -> service.validate(sample, "operator.liverpool"))
                .isInstanceOf(SampleValidationException.class)
                .hasMessage("Office location is required.");
    }

    @Test
    void processSamplePersistsSampleAndAuditLogInTransaction() {
        UUID persistedId = UUID.fromString("8ad98074-aa21-4a5e-a3ff-18896f1b5c33");
        LabSample sample = validSample("12.5000");
        LabSample persisted = sample.withSampleId(persistedId);
        when(labSampleRepository.save(sample)).thenReturn(persisted);

        LabSample result = service.processSample(sample, " operator.liverpool ");

        assertThat(result.sampleId()).isEqualTo(persistedId);
        ArgumentCaptor<com.ahk.samples.domain.AuditLog> auditCaptor =
                ArgumentCaptor.forClass(com.ahk.samples.domain.AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertThat(auditCaptor.getValue().sampleId()).isEqualTo(persistedId);
        assertThat(auditCaptor.getValue().createdBy()).isEqualTo("operator.liverpool");
        assertThat(auditCaptor.getValue().eventType()).isEqualTo("SAMPLE_RECEIVED");
    }

    @Test
    void processSampleDoesNotTouchDatabaseWhenValidationFails() {
        LabSample sample = validSample("-1.0000");

        assertThatThrownBy(() -> service.processSample(sample, "operator.liverpool"))
                .isInstanceOf(SampleValidationException.class);

        verify(labSampleRepository, never()).save(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void crisisSituationDocumentsDatabaseTimeoutDuringHighPriorityMineralUpload() {
        LabSample sample = validSample("42.0000");
        when(labSampleRepository.save(sample))
                .thenThrow(new QueryTimeoutException("PostgreSQL statement timeout"));

        assertThatThrownBy(() -> service.processSample(sample, "operator.liverpool"))
                .isInstanceOf(CrisisResolutionException.class)
                .hasMessage("Critical database timeout while processing high-value sample upload.")
                .extracting("defectReference")
                .isEqualTo("CRISIS-DB-TIMEOUT");
    }

    private static LabSample validSample(String analysisWeight) {
        return LabSample.received(
                "AHK-MIN-001",
                "Copper Concentrate",
                "Liverpool, UK",
                SamplePriority.HIGH,
                new BigDecimal(analysisWeight),
                OffsetDateTime.now(CLOCK)
        );
    }
}
