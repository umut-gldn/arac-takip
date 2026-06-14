package com.umutgldn.tracking_service.exception;

public class InvalidSimulationStateException extends RuntimeException {
    public InvalidSimulationStateException(String message) {
        super(message);
    }
}
