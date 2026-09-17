package com.wisp.app;

public class NegocioException extends RuntimeException {

    public NegocioException(String message) {
        super(message);
    }
}