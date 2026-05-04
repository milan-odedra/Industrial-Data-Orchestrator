package com.ahk.samples.repository;

import com.ahk.samples.domain.AuditLog;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAuditLogRepository implements AuditLogRepository {
    private static final String INSERT_SQL = """
            INSERT INTO audit_logs (
                sample_id,
                event_type,
                event_payload,
                created_by,
                created_at
            ) VALUES (
                :sampleId,
                :eventType,
                CAST(:eventPayload AS jsonb),
                :createdBy,
                :createdAt
            )
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcAuditLogRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(AuditLog auditLog) {
        jdbcTemplate.update(
                INSERT_SQL,
                new MapSqlParameterSource()
                        .addValue("sampleId", auditLog.sampleId())
                        .addValue("eventType", auditLog.eventType())
                        .addValue("eventPayload", auditLog.eventPayload())
                        .addValue("createdBy", auditLog.createdBy())
                        .addValue("createdAt", auditLog.createdAt())
        );
    }
}
