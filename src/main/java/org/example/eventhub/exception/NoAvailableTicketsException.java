package org.example.eventhub.exception;

public class NoAvailableTicketsException extends RuntimeException {
    public NoAvailableTicketsException(String message) {
        super(message);
    }
}
