package com.example.inventory.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryExceptionHandler.class);

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleExecutionFailure(IllegalStateException exception) {
        LOG.error("Inventory execution failed", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Inventory execution failed.");
    }
}
