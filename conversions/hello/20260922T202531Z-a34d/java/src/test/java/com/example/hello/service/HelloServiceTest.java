package com.example.hello.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HelloServiceTest {

    private final HelloService helloService = new HelloService();
    
    @Test
    void retainsNoStateBetweenInvocations() {
        assertThat(helloService.getMessage()).isEqualTo(helloService.getMessage());
    }
}
