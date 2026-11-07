package com.example.payroll.model;

import java.math.BigDecimal;

public record EmployeePayroll(
        Employee employee,
        BigDecimal bonusAmount,
        BigDecimal overtimePay,
        BigDecimal grossSalary,
        BigDecimal insuranceAmount,
        BigDecimal taxAmount,
        BigDecimal netSalary) {
}
