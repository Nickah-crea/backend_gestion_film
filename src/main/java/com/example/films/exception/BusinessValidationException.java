package com.example.films.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessValidationException extends RuntimeException {
    
    public BusinessValidationException(String message) {
        super(message);
    }
    
    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}