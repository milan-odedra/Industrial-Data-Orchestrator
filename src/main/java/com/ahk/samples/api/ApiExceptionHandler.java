package com.ahk.samples.api;

import com.ahk.samples.service.CrisisResolutionException;
import com.ahk.samples.service.SampleValidationException;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private final Clock clock;

    public ApiExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(SampleValidationException.class)
    ResponseEntity<ErrorResponse> handleValidation(SampleValidationException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("SAMPLE_VALIDATION_FAILED", ex.getMessage(), OffsetDateTime.now(clock)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("MALFORMED_REQUEST", "Sample request body is invalid.", OffsetDateTime.now(clock)));
    }

    @ExceptionHandler(CrisisResolutionException.class)
    ResponseEntity<ErrorResponse> handleCrisis(CrisisResolutionException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(ex.defectReference(), ex.getMessage(), OffsetDateTime.now(clock)));
    }
}
