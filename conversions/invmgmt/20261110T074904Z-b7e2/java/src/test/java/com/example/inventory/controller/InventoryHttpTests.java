package com.example.inventory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.inventory.exception.InventoryExceptionHandler;
import com.example.inventory.service.InventoryService;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryHttpTests {
    @LocalServerPort
    private int port;

    @Test
    void servesTheCompleteBatchOverRealHttpAndResetsBetweenRequests() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(endpoint("/api/inventory/runs"))
                    .POST(HttpRequest.BodyPublishers.noBody()).build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.headers().firstValue("Content-Type").orElseThrow())
                    .startsWith("application/json");
            var json = JsonPath.parse(response.body());
            assertThat(json.read("$.products.length()", Integer.class)).isEqualTo(10);
            assertThat(json.read("$.transactions.length()", Integer.class)).isEqualTo(12);
            assertThat(json.read("$.products[0].name", String.class)).isEqualTo("WIRELESS MOUSE      ");
            assertThat(json.read("$.products[0].quantity", Integer.class)).isEqualTo(40);
            assertThat(json.read("$.summary.processed", Integer.class)).isEqualTo(8);
            assertThat(json.read("$.summary.rejected", Integer.class)).isEqualTo(4);
            assertThat(json.read("$.summary.netValue", Double.class)).isEqualTo(27676.07);
            assertThat(json.read("$.summary.taxAmount", Double.class)).isEqualTo(4981.69);
            assertThat(json.read("$.summary.grossValue", Double.class)).isEqualTo(32657.76);
            assertThat(json.read("$.transactions[2].applied", Boolean.class)).isFalse();
            assertThat(json.read("$.transactions[2].rejectionReason", String.class))
                    .isEqualTo("INSUFFICIENT STOCK");
            assertThat(json.read("$.report", String.class))
                    .startsWith("-".repeat(78) + "\nTRANSACTION PROCESSING LOG\n")
                    .endsWith("ACTION: PLEASE REVIEW LOW STOCK ITEMS.\n");
            assertThat(client.send(request, HttpResponse.BodyHandlers.ofString()).body())
                    .isEqualTo(response.body());
        }
    }

    @Test
    void unsupportedMethodsAndRoutesAreNotSuccessfulRuns() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var wrongMethod = HttpRequest.newBuilder(endpoint("/api/inventory/runs")).GET().build();
            assertThat(client.send(wrongMethod, HttpResponse.BodyHandlers.ofString()).statusCode())
                    .isEqualTo(405);
            var unknown = HttpRequest.newBuilder(endpoint("/api/inventory/missing")).GET().build();
            assertThat(client.send(unknown, HttpResponse.BodyHandlers.ofString()).statusCode())
                    .isEqualTo(404);
        }
    }
}
