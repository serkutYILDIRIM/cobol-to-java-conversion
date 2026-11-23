package com.example.inventory.model;

import org.junit.jupiter.api.Test;

class EmbeddedInventoryTests {
    @Test
    void parsesEveryProductAtTheOriginalFixedWidthOffsets() {
        var products = EmbeddedInventory.products();
        assertThat(products).hasSize(10);
        assertThat(products).extracting(Product::code).containsExactly(
                "P1001", "P1002", "P1003", "P1004", "P1005",
                "P1006", "P1007", "P1008", "P1009", "P1010");
}
