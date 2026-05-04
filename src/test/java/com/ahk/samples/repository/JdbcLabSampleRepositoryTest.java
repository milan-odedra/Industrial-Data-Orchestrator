package com.ahk.samples.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ahk.samples.domain.LabSample;
import com.ahk.samples.domain.SamplePriority;
import java.math.BigDecimal;
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
class JdbcLabSampleRepositoryTest {
    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    void saveMapsSampleFieldsToPostgresInsert() {
        UUID sampleId = UUID.fromString("eeb84c29-80f2-4f71-9611-98bc2893a0bf");
        LabSample sample = LabSample.received(
                "AHK-AGRI-001",
                "Soybean Meal",
                "Callao, Peru",
                SamplePriority.STANDARD,
                new BigDecimal("3.2500"),
                OffsetDateTime.parse("2026-05-04T09:30:00Z")
        );
        when(jdbcTemplate.queryForObject(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(MapSqlParameterSource.class),
                eq(UUID.class)
        )).thenReturn(sampleId);

        LabSample persisted = new JdbcLabSampleRepository(jdbcTemplate).save(sample);

        assertThat(persisted.sampleId()).isEqualTo(sampleId);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).queryForObject(org.mockito.ArgumentMatchers.anyString(), paramsCaptor.capture(), eq(UUID.class));
        assertThat(paramsCaptor.getValue().getValue("externalReference")).isEqualTo("AHK-AGRI-001");
        assertThat(paramsCaptor.getValue().getValue("officeLocation")).isEqualTo("Callao, Peru");
        assertThat(paramsCaptor.getValue().getValue("status")).isEqualTo("RECEIVED");
        assertThat(paramsCaptor.getValue().getValue("analysisWeight")).isEqualTo(new BigDecimal("3.2500"));
    }
}
