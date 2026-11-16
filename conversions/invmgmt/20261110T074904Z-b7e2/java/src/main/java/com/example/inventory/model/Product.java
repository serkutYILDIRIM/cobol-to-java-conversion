package com.example.inventory.model;

public record Product(String code, String name, int quantity, int reorderLevel,
                      BigDecimal unitPrice, String status) {
}
