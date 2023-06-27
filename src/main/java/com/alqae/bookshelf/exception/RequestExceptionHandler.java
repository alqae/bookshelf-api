package com.alqae.bookshelf.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class RequestExceptionHandler {
    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<Exception> handleRequestException(RequestException ex) {
        Exception exception = Exception.builder()
                .message(ex.getMessage())
                .throwable(ex.getCause())
                .httpStatus(ex.getStatus())
                .timestamp(ZonedDateTime.now())
                .build();

        return new ResponseEntity<>(exception, ex.getStatus());
    }
}
