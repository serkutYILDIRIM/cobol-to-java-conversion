package com.example.inventory.dto;

import java.math.BigDecimal;
import java.util.List;

public record InventoryRunResponse(List<ProductResult> products,
                                   List<TransactionResult> transactions,
                                   Summary summary, String report) {
    public InventoryRunResponse {
        products = List.copyOf(products);
        transactions = List.copyOf(transactions);
    }
}
