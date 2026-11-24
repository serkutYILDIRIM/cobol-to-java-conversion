package com.example.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.example.inventory.dto.InventoryRunResponse.ProductResult;
import com.example.inventory.dto.InventoryRunResponse.TransactionResult;
import com.example.inventory.mapper.InventoryReportFormatter;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InventoryServiceTests {
    private final InventoryService service = new InventoryService(new InventoryReportFormatter());

    @Test
    void executesTheEntireSourceBatch() {
        var result = service.run();
        assertThat(result.products()).extracting(ProductResult::quantity)
                .containsExactly(40, 85, 18, 50, 55, 42, 15, 7, 8, 80);
        assertThat(result.products()).extracting(ProductResult::flag)
                .containsExactly("LOW", "OK", "LOW", "LOW", "OK", "OK", "LOW", "LOW", "INACT", "LOW");
        assertThat(result.products()).extracting(ProductResult::stockValue).containsExactly(
                new BigDecimal("996.00"), new BigDecimal("7607.50"), new BigDecimal("4499.82"),
                new BigDecimal("1700.00"), new BigDecimal("2351.25"), new BigDecimal("6678.00"),
                new BigDecimal("898.50"), new BigDecimal("833.00"), new BigDecimal("1512.00"),
                new BigDecimal("600.00"));
        assertThat(result.transactions().stream().filter(t -> !t.applied()))
                .extracting(TransactionResult::rejectionReason).containsExactly(
                        "INSUFFICIENT STOCK", "PRODUCT NOT FOUND",
                        "INVALID TRANSACTION TYPE", "PRODUCT IS INACTIVE");
        var summary = result.summary();
        assertThat(summary.processed()).isEqualTo(8);
        assertThat(summary.rejected()).isEqualTo(4);
        assertThat(summary.totalReceived()).isEqualTo(70);
        assertThat(summary.totalSold()).isEqualTo(488);
        assertThat(summary.lowStockCount()).isEqualTo(6);
        assertThat(summary.netValue()).isEqualByComparingTo("27676.07");
        assertThat(summary.taxAmount()).isEqualByComparingTo("4981.69");
        assertThat(summary.grossValue()).isEqualByComparingTo("32657.76");
    }

    @Test
    void preservesRejectionPrecedenceAndNeverMutatesRejectedStock() {
        var result = service.process(List.of(product("P0001", 5, 0, "I"),
                        product("P0002", 5, 0, "A")),
                List.of(new Transaction("P9999", "X", 99),
                        new Transaction("P0001", "X", 99),
                        new Transaction("P0002", "X", 99),
                        new Transaction("P0002", "S", 6)));
        assertThat(result.transactions()).extracting(TransactionResult::rejectionReason)
                .containsExactly("PRODUCT NOT FOUND", "PRODUCT IS INACTIVE",
                        "INVALID TRANSACTION TYPE", "INSUFFICIENT STOCK");
        assertThat(result.products()).extracting(ProductResult::quantity).containsExactly(5, 5);
        assertThat(result.summary().processed()).isZero();
        assertThat(result.summary().rejected()).isEqualTo(4);
        assertThat(result.summary().totalReceived()).isZero();
        assertThat(result.summary().totalSold()).isZero();
    }
}
