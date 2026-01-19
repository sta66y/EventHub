package org.example.eventhub.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
