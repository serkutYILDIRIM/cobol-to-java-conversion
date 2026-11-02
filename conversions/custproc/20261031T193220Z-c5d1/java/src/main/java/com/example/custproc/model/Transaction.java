package com.example.custproc.model;

import java.math.BigDecimal;

/** One TRANS-REC: fixed 10-character customer id, type code, unsigned amount with scale 2. */
public record Transaction(String customerId, char type, BigDecimal amount) {

    public boolean isDeposit() {
        return type == 'D';
    }

    public boolean isWithdrawal() {
        return type == 'W';
    }

    public boolean isFee() {
        return type == 'F';
    }
}
