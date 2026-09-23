package com.example.hello.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private static final String COBOL_MESSAGE = "HELLO FROM IBM COBOL          ";

    public String getMessage() {
        return COBOL_MESSAGE;
    }
}
