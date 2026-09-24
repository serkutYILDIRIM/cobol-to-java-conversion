package com.example.hello.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HelloServiceTest {

    private final HelloService helloService = new HelloService();

    @Test
    void returnsTheThirtyCharacterCobolDisplayField() {
        assertThat(helloService.getMessage())
                .isEqualTo("HELLO FROM IBM COBOL          ")
                .hasSize(30);
    }

    @Test
    void retainsNoStateBetweenInvocations() {
        assertThat(helloService.getMessage()).isEqualTo(helloService.getMessage());
    }
}
