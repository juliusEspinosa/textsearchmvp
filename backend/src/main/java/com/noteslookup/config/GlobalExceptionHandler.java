package com.noteslookup.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        problem.setTitle("Bad Request");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now());

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        problem.setProperty("errors", errors);
        return problem;
    }

    /**
     * Maps exception types to HTTP problem details using pattern matching for switch.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParam(MissingServletRequestParameterException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problem.setTitle("Bad Request");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        return switch (ex) {
            case ItemNotFoundException notFound -> {
                var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, notFound.getMessage());
                problem.setTitle("Not Found");
                problem.setProperty("timestamp", Instant.now());
                yield problem;
            }
            case PurchaseNotFoundException notFound -> {
                var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, notFound.getMessage());
                problem.setTitle("Not Found");
                problem.setProperty("timestamp", Instant.now());
                yield problem;
            }
            case PokemonNotFoundException notFound -> {
                var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, notFound.getMessage());
                problem.setTitle("Not Found");
                problem.setProperty("timestamp", Instant.now());
                yield problem;
            }
            case DigimonNotFoundException notFound -> {
                var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, notFound.getMessage());
                problem.setTitle("Not Found");
                problem.setProperty("timestamp", Instant.now());
                yield problem;
            }
            case IllegalArgumentException iae -> {
                var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, iae.getMessage());
                problem.setTitle("Bad Request");
                problem.setProperty("timestamp", Instant.now());
                yield problem;
            }
            default -> {
                var problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred"
                );
                problem.setTitle("Internal Server Error");
                problem.setProperty("timestamp", Instant.now());
                yield problem;
            }
        };
    }
}
