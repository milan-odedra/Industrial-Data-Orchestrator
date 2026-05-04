package com.ahk.samples.repository;

import com.ahk.samples.domain.AuditLog;

public interface AuditLogRepository {
    void save(AuditLog auditLog);
}
