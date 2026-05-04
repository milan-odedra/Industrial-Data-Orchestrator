package com.ahk.samples.api;

import com.ahk.samples.domain.LabSample;
import com.ahk.samples.service.SampleTrackingService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/samples")
public class SampleController {
    private final SampleTrackingService sampleTrackingService;

    public SampleController(SampleTrackingService sampleTrackingService) {
        this.sampleTrackingService = sampleTrackingService;
    }

    @PostMapping
    public ResponseEntity<SampleResponse> processSample(@RequestBody ProcessSampleRequest request) {
        LabSample sample = LabSample.received(
                request.externalReference(),
                request.commodityType(),
                request.officeLocation(),
                request.priority(),
                request.analysisWeight(),
                request.receivedAt()
        );
        LabSample persistedSample = sampleTrackingService.processSample(sample, request.operatorId());
        return ResponseEntity
                .created(URI.create("/api/samples/" + persistedSample.sampleId()))
                .body(SampleResponse.from(persistedSample));
    }
}
