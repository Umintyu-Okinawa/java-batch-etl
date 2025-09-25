package com.example.batch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")   // ← application-test.yml を使う
class JavaBatchEtlAiApplicationTests {
    @Test
    void contextLoads() {
    }
}
