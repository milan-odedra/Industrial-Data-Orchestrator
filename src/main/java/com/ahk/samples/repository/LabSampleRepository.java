package com.ahk.samples.repository;

import com.ahk.samples.domain.LabSample;

public interface LabSampleRepository {
    LabSample save(LabSample sample);
}
