package com.example.inventory.model;

import java.math.BigDecimal;

public record Product(String code, String name, int quantity, int reorderLevel,
                      BigDecimal unitPrice, String status) {
}
