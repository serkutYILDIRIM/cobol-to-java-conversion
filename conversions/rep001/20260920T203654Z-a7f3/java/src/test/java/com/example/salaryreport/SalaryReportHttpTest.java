package com.example.salaryreport;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SalaryReportHttpTest {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void returnsDownloadableUtf8Report() throws Exception {
        String record = TestRecords.employee("00001", "ÇAĞRI", "ÖZTÜRK", "AR-GE", "00123456");

        HttpResponse<byte[]> response = post(record.getBytes(StandardCharsets.UTF_8));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValue("text/plain;charset=UTF-8");
        assertThat(response.headers().firstValue("Content-Disposition"))
                .hasValue("attachment; filename=\"EMP_REPORT.TXT\"");
        assertThat(new String(response.body(), StandardCharsets.UTF_8)).contains("ÇAĞRI ÖZTÜRK");
    }

    @Test
    void mapsMalformedRecordsToProblemDetail() throws Exception {
        HttpResponse<byte[]> response = post("too short".getBytes(StandardCharsets.UTF_8));
        String problem = new String(response.body(), StandardCharsets.UTF_8);

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.headers().firstValue("Content-Type").orElse("")).startsWith("application/problem+json");
        assertThat(problem).contains("Invalid employee data").contains("exactly 80 characters");
    }

    @Test
    void mapsNumericOverflowToUnprocessableContent() throws Exception {
        String record = TestRecords.employee("00001", "A", "B", "D", "99999999");
        String input = String.join("\n", java.util.Collections.nCopies(1001, record));

        HttpResponse<byte[]> response = post(input.getBytes(StandardCharsets.UTF_8));

        assertThat(response.statusCode()).isEqualTo(422);
        assertThat(new String(response.body(), StandardCharsets.UTF_8)).contains("Numeric capacity exceeded");
    }

    private HttpResponse<byte[]> post(byte[] body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:%d/api/v1/salary-reports".formatted(port)))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
