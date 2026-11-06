package com.example.payroll.model;

import java.math.BigDecimal;

public record Employee(
        int id,
        String name,
        String department,
        BigDecimal baseSalary,
        int overtimeHours,
        int performanceScore) {
}
