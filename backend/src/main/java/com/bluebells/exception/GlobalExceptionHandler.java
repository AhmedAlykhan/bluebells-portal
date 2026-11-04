package com.bluebells.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("API Exception [{}]: {}", correlationId, ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                ex.getStatus().getReasonPhrase(),
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Validation error [{}]: {} fields failed validation", correlationId, ex.getBindingResult().getErrorCount());

        ErrorResponse error = new ErrorResponse(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "One or more input fields failed validation constraints",
                request.getRequestURI(),
                correlationId
        );

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getValidationErrors().add(new ValidationError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage(),
                    fieldError.getRejectedValue()
            ));
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Access Denied [{}]: uri={}", correlationId, request.getRequestURI());

        ErrorResponse error = new ErrorResponse(
                "Forbidden",
                HttpStatus.FORBIDDEN.value(),
                "You do not possess the required permissions to access this resource",
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Authentication failed [{}]: {}", correlationId, ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid authentication credentials or expired session",
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(OptimisticLockingFailureException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Concurrency conflict [{}]: resource was modified concurrently", correlationId);

        ErrorResponse error = new ErrorResponse(
                "Conflict",
                HttpStatus.CONFLICT.value(),
                "The requested resource was modified by another concurrent transaction. Please refresh and retry.",
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.error("Data integrity violation [{}]: {}", correlationId, ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Conflict",
                HttpStatus.CONFLICT.value(),
                "Database constraint violation (such as duplicate unique key or foreign key dependency).",
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Method not supported [{}]: uri={}, method={}", correlationId, request.getRequestURI(), ex.getMethod());

        ErrorResponse error = new ErrorResponse(
                "Method Not Allowed",
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                        ex.getMethod(), ex.getSupportedHttpMethods()),
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Malformed HTTP request body [{}]: {}", correlationId, ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                "Malformed request payload or invalid data format",
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Method argument type mismatch [{}]: parameter={}, value={}", correlationId, ex.getName(), ex.getValue());

        ErrorResponse error = new ErrorResponse(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                        ex.getValue(), ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"),
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.warn("Resource not found [{}]: uri={}", correlationId, request.getRequestURI());

        ErrorResponse error = new ErrorResponse(
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                "The requested resource endpoint does not exist",
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.error("Unhandled server exception [{}]: ", correlationId, ex);

        ErrorResponse error = new ErrorResponse(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected internal error occurred. Please contact the administrator with the correlation ID.",
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
