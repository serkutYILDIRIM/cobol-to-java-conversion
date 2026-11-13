package com.example.inventory.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryExceptionHandler.class);

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleExecutionFailure(IllegalStateException exception) {

    }
}
