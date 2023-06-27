package com.alqae.bookshelf.exception;

import org.springframework.http.HttpStatus;

public class RequestException extends RuntimeException {
    private HttpStatus status;

    public RequestException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public RequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
