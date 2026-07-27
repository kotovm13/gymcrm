package com.example.gymcrm.rest.exception;

import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.rest.filter.RestLoggingFilter;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException exception) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage(), exception);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        String transactionId = MDC.get(RestLoggingFilter.TRANSACTION_ID);
        LOGGER.error("Unexpected REST failure transactionId={}", transactionId, exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Unexpected server error", transactionId));
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, Exception exception) {
        String transactionId = MDC.get(RestLoggingFilter.TRANSACTION_ID);
        LOGGER.warn("REST call failed transactionId={}, status={}, exception={}, message={}",
                transactionId, status.value(), exception.getClass().getSimpleName(), message);
        return ResponseEntity.status(status).body(new ErrorResponse(message, transactionId));
    }
}
