package com.example.salaryreport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidEmployeeDataException.class)
    ProblemDetail invalidEmployeeData(InvalidEmployeeDataException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid employee data", exception.getMessage());
    }

    @ExceptionHandler(NumericCapacityException.class)
    ProblemDetail numericCapacity(NumericCapacityException exception) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Numeric capacity exceeded", exception.getMessage());
    }

    @ExceptionHandler(ReportIoException.class)
    ProblemDetail reportIoFailure() {
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Report processing failed",
                "The employee data could not be processed because of an I/O failure.");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
