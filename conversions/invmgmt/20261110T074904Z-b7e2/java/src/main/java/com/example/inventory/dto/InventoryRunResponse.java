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

    public record ProductResult(String code, String name, int quantity, int reorderLevel,
                                BigDecimal unitPrice, String status,
                                BigDecimal stockValue, String flag) {
    }

    public record TransactionResult(String code, String type, int quantity,
                                    boolean applied, String rejectionReason) {
    }

}
