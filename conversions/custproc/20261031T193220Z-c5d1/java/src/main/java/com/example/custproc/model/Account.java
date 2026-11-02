package com.example.custproc.model;

import java.math.BigDecimal;

/** Master record data: 10-character id, 30-character name, balance S9(9)V99, status code. */
public record Account(String customerId, String name, BigDecimal balance, char status) {

    public boolean isFrozen() {
        return status == 'F';
    }

    public Account withBalance(BigDecimal newBalance) {
        return new Account(customerId, name, newBalance, status);
    }
}
