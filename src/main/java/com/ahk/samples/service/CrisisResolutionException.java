package com.ahk.samples.service;

public class CrisisResolutionException extends RuntimeException {
    private final String defectReference;

    public CrisisResolutionException(String message, String defectReference, Throwable cause) {
        super(message, cause);
        this.defectReference = defectReference;
    }

    public String defectReference() {
        return defectReference;
    }
}
