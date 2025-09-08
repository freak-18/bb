package com.examly.springapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(String message) {}

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST; // Default to 400

        if (message.toLowerCase().contains("not found")) {
            status = HttpStatus.NOT_FOUND; // Use 404 for "not found" errors
        }

        return new ResponseEntity<>(new ErrorResponse(message), status);
    }
}
