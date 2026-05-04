package com.ahk.samples.service;

import com.ahk.samples.domain.AuditLog;
import com.ahk.samples.domain.LabSample;
import com.ahk.samples.repository.AuditLogRepository;
import com.ahk.samples.repository.LabSampleRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class SampleTrackingService {
    private static final int MAX_REFERENCE_LENGTH = 128;
    private static final int MAX_LOCATION_LENGTH = 96;

    private final LabSampleRepository labSampleRepository;
    private final AuditLogRepository auditLogRepository;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    public SampleTrackingService(
            LabSampleRepository labSampleRepository,
            AuditLogRepository auditLogRepository,
            TransactionTemplate transactionTemplate,
            Clock clock
    ) {
        this.labSampleRepository = Objects.requireNonNull(labSampleRepository, "labSampleRepository must not be null");
        this.auditLogRepository = Objects.requireNonNull(auditLogRepository, "auditLogRepository must not be null");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public LabSample processSample(LabSample sample, String operatorId) {
        validate(sample, operatorId);

        try {
            return transactionTemplate.execute(status -> {
                LabSample persistedSample = labSampleRepository.save(sample);
                auditLogRepository.save(AuditLog.sampleReceived(
                        persistedSample,
                        operatorId.trim(),
                        OffsetDateTime.now(clock)
                ));
                return persistedSample;
            });
        } catch (QueryTimeoutException ex) {
            throw new CrisisResolutionException(
                    "Critical database timeout while processing high-value sample upload.",
                    "CRISIS-DB-TIMEOUT",
                    ex
            );
        } catch (TransactionException ex) {
            throw new CrisisResolutionException(
                    "Transactional persistence failed while processing laboratory sample.",
                    "CRISIS-TX-FAILURE",
                    ex
            );
        }
    }

    public void validate(LabSample sample, String operatorId) {
        if (sample == null) {
            throw new SampleValidationException("Sample payload is required.");
        }
        requireText(sample.externalReference(), "External reference is required.");
        requireText(sample.commodityType(), "Commodity type is required.");
        requireText(sample.officeLocation(), "Office location is required.");
        requireText(operatorId, "Operator id is required.");

        if (sample.externalReference().length() > MAX_REFERENCE_LENGTH) {
            throw new SampleValidationException("External reference must be 128 characters or fewer.");
        }
        if (sample.officeLocation().length() > MAX_LOCATION_LENGTH) {
            throw new SampleValidationException("Office location must be 96 characters or fewer.");
        }
        if (sample.status() == null) {
            throw new SampleValidationException("Sample status is required.");
        }
        if (sample.priority() == null) {
            throw new SampleValidationException("Sample priority is required.");
        }
        if (sample.receivedAt() == null) {
            throw new SampleValidationException("Received timestamp is required.");
        }
        if (sample.analysisWeight() == null) {
            throw new SampleValidationException("Analysis weight is required.");
        }
        if (sample.analysisWeight().compareTo(BigDecimal.ZERO) < 0) {
            throw new SampleValidationException("Analysis weight cannot be negative.");
        }
    }

    private static void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new SampleValidationException(message);
        }
    }
}
