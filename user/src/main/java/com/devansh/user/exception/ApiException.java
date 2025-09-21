package com.devansh.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiException(HttpStatus status, String message, Throwable cause) {
        super(cause);
        this.status = status;
        this.message = message;
    }
}
