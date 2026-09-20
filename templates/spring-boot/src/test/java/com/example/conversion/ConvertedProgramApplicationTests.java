package com.example.conversion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvertedProgramApplicationTests {

    @Test
    void applicationStarts() {
        // Context and embedded-server startup failures fail this smoke test.
    }
}
