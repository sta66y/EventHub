package org.example.eventhub.exception;

public class EventAlreadyExistsException extends RuntimeException {
    public EventAlreadyExistsException(String message) {
        super(message);
    }
}
