package com.example.inventory.model;

import java.math.BigDecimal;

public record Product(String code, String name, int quantity, int reorderLevel,
                      BigDecimal unitPrice, String status) {
    public Product withQuantity(int newQuantity) {
        return new Product(code, name, newQuantity, reorderLevel, unitPrice, status);
    }
}
