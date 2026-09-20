package com.example.salaryreport.model;

import java.math.BigDecimal;

public record Employee(
        String id,
        String firstName,
        String lastName,
        String department,
        BigDecimal salary) {
}
