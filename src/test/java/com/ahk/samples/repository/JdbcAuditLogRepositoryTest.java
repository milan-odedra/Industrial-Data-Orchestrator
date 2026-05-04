package com.ahk.samples.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import com.ahk.samples.domain.AuditLog;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ExtendWith(MockitoExtension.class)
class JdbcAuditLogRepositoryTest {
    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    void saveMapsAuditLogFieldsToPostgresInsert() {
        UUID sampleId = UUID.fromString("eeb84c29-80f2-4f71-9611-98bc2893a0bf");
        AuditLog auditLog = new AuditLog(
                null,
                sampleId,
                "SAMPLE_RECEIVED",
                "{\"status\":\"RECEIVED\"}",
                "operator.liverpool",
                OffsetDateTime.parse("2026-05-04T09:30:00Z")
        );

        new JdbcAuditLogRepository(jdbcTemplate).save(auditLog);

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).update(anyString(), paramsCaptor.capture());
        assertThat(paramsCaptor.getValue().getValue("sampleId")).isEqualTo(sampleId);
        assertThat(paramsCaptor.getValue().getValue("eventType")).isEqualTo("SAMPLE_RECEIVED");
        assertThat(paramsCaptor.getValue().getValue("createdBy")).isEqualTo("operator.liverpool");
    }
}
