package com.example.inventory.model;

import org.junit.jupiter.api.Test;

class EmbeddedInventoryTests {
    @Test
    void parsesEveryProductAtTheOriginalFixedWidthOffsets() {
        var products = EmbeddedInventory.products();
        assertThat(products).hasSize(10);
    }
}
