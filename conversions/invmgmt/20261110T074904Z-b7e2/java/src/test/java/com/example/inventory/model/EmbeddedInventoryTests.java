package com.example.inventory.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EmbeddedInventoryTests {
    @Test
    void parsesEveryProductAtTheOriginalFixedWidthOffsets() {
        var products = EmbeddedInventory.products();
        assertThat(products).hasSize(10);
        assertThat(products).extracting(Product::code).containsExactly(
                "P1001", "P1002", "P1003", "P1004", "P1005",
                "P1006", "P1007", "P1008", "P1009", "P1010");
        assertThat(products).extracting(Product::quantity)
                .containsExactly(120, 45, 18, 200, 60, 12, 35, 25, 8, 300);
        assertThat(products).extracting(Product::reorderLevel)
                .containsExactly(50, 30, 20, 75, 25, 15, 20, 10, 10, 100);
        assertThat(products).extracting(Product::unitPrice).containsExactly(
                new BigDecimal("24.90"), new BigDecimal("89.50"), new BigDecimal("249.99"),
                new BigDecimal("34.00"), new BigDecimal("42.75"), new BigDecimal("159.00"),
                new BigDecimal("59.90"), new BigDecimal("119.00"), new BigDecimal("189.00"),
                new BigDecimal("7.50"));
        assertThat(products).extracting(Product::name).containsExactly(
                "WIRELESS MOUSE      ", "MECHANICAL KEYBOARD ", "27 INCH MONITOR     ",
                "USB-C HUB           ", "LAPTOP STAND        ", "NOISE CANCEL HEADSET",
                "HD WEBCAM           ", "EXTERNAL SSD 1TB    ", "DOCKING STATION     ",
                "HDMI CABLE 2M       ");
        assertThat(products).extracting(Product::status)
                .containsExactly("A", "A", "A", "A", "A", "A", "A", "A", "I", "A");
    }

    @Test
    void parsesSignedTransactionsWithoutLosingOrderOrUnknownTypes() {
        var transactions = EmbeddedInventory.transactions();
        assertThat(transactions).hasSize(12);
        assertThat(transactions).extracting(Transaction::quantity)
                .containsExactly(80, 40, 25, 150, -5, 10, 30, 5, 2, 18, 220, 20);
        assertThat(transactions).extracting(Transaction::type)
                .containsExactly("S", "R", "S", "S", "A", "R", "R", "X", "S", "S", "S", "S");
        assertThat(transactions).extracting(Transaction::code).containsExactly(
                "P1001", "P1002", "P1003", "P1004", "P1005", "P9999",
                "P1006", "P1007", "P1009", "P1008", "P1010", "P1007");
    }
}
