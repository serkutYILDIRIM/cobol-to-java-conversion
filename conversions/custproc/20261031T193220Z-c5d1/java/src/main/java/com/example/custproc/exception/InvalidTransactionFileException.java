package com.example.custproc.exception;

public class InvalidTransactionFileException extends RuntimeException {

    public InvalidTransactionFileException(String message) {
        super(message);
    }
}
