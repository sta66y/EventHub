package org.example.eventhub.controller;

import org.example.eventhub.exception.*;
import org.example.eventhub.model.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AppError> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new AppError(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AppError> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(new AppError(ex.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<AppError> handleEventNotFound(EventNotFoundException ex) {
        return new ResponseEntity<>(new AppError(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventAlreadyExistsException.class)
    public ResponseEntity<AppError> handleEventAlreadyExists(EventAlreadyExistsException ex) {
        return new ResponseEntity<>(new AppError(ex.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAvailableTicketsException.class)
    public ResponseEntity<AppError> handleNoAvailableTickets(NoAvailableTicketsException ex) {
        return new ResponseEntity<>(new AppError(ex.getMessage(), HttpStatus.CONFLICT.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<AppError> handleOrderNotFound(OrderNotFoundException ex) {
        return new ResponseEntity<>(new AppError(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<AppError> handleTicketNotFound(TicketNotFoundException ex) {
        return new ResponseEntity<>(new AppError(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
