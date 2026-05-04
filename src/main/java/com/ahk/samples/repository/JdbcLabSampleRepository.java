package com.ahk.samples.repository;

import com.ahk.samples.domain.LabSample;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLabSampleRepository implements LabSampleRepository {
    private static final String INSERT_SQL = """
            INSERT INTO lab_samples (
                external_reference,
                commodity_type,
                office_location,
                status,
                priority,
                analysis_weight,
                received_at
            ) VALUES (
                :externalReference,
                :commodityType,
                :officeLocation,
                :status,
                :priority,
                :analysisWeight,
                :receivedAt
            )
            RETURNING sample_id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcLabSampleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LabSample save(LabSample sample) {
        UUID sampleId = jdbcTemplate.queryForObject(
                INSERT_SQL,
                new MapSqlParameterSource()
                        .addValue("externalReference", sample.externalReference())
                        .addValue("commodityType", sample.commodityType())
                        .addValue("officeLocation", sample.officeLocation())
                        .addValue("status", sample.status().name())
                        .addValue("priority", sample.priority().name())
                        .addValue("analysisWeight", sample.analysisWeight())
                        .addValue("receivedAt", sample.receivedAt()),
                UUID.class
        );
        return sample.withSampleId(sampleId);
    }
}
