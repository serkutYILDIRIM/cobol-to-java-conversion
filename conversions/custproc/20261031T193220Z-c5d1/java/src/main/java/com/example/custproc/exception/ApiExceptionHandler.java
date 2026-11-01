package com.example.custproc.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(InvalidTransactionFileException.class)
    ProblemDetail invalidTransactions(InvalidTransactionFileException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid transaction file");
        return problem;
    }

    @ExceptionHandler(MasterFileException.class)
    ProblemDetail masterFile(MasterFileException ex) {
        LOG.error("Master file failure", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, "The customer master file is unavailable.");
        problem.setTitle("Master file unavailable");
        return problem;
    }
}
