package com.alqae.bookshelf.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exception {
    private String message;
    private Throwable throwable;
    private HttpStatus httpStatus;
    private ZonedDateTime timestamp;
}
