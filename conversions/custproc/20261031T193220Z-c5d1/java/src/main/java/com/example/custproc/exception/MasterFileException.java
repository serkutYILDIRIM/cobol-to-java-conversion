package com.example.custproc.exception;

/** Master file could not be opened or accessed; replaces the COBOL open-failure STOP RUN. */
public class MasterFileException extends RuntimeException {

    public MasterFileException(String message) {
        super(message);
    }

    public MasterFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
