package com.example.payroll.model;

import java.math.BigDecimal;
import java.util.List;

public record PayrollReport(
        List<EmployeePayroll> employees,
        BigDecimal totalGross,
        BigDecimal totalDeductions,
        BigDecimal totalNet,
        BigDecimal averageNet,
        String highestPaidEmployee) {

    public PayrollReport {
        employees = List.copyOf(employees);
    }
}
