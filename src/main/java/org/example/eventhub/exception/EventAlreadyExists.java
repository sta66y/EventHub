package org.example.eventhub.exception;

public class EventAlreadyExists extends RuntimeException {
    public EventAlreadyExists(String message) {
        super(message);
    }
}
