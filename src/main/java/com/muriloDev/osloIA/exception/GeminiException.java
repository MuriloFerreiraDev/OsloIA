package com.muriloDev.osloIA.exception;

public class GeminiException extends RuntimeException {
    private final int statusCode;

    public GeminiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}