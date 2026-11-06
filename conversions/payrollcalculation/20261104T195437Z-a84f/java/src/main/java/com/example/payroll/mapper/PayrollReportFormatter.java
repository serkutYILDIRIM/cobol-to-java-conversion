package com.example.payroll.mapper;

import com.example.payroll.model.EmployeePayroll;
import com.example.payroll.model.PayrollReport;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class PayrollReportFormatter {

    private static final String SEPARATOR = "-".repeat(50);

    public String format(PayrollReport report) {
        List<String> lines = new ArrayList<>();
        lines.add(SEPARATOR);
        lines.add("MONTHLY SALARY AND BONUS REPORT");
        lines.add("Currency: TRY");
        lines.add("Sample rates: insurance 14%, tax 15%");
        lines.add(SEPARATOR);

        for (EmployeePayroll payroll : report.employees()) {
            lines.add("Employee ID: " + String.format(Locale.ROOT, "%04d", payroll.employee().id()));
            lines.add("Employee: " + payroll.employee().name().trim());
            lines.add("Department: " + payroll.employee().department().trim());
            lines.add("Performance (1-5): " + payroll.employee().performanceScore());
            lines.add("Overtime (hours): "
                    + String.format(Locale.ROOT, "%03d", payroll.employee().overtimeHours()));
            lines.add("Base salary: " + formatAmount(payroll.employee().baseSalary()));
            lines.add("Bonus:       " + formatAmount(payroll.bonusAmount()));
            lines.add("Overtime:    " + formatAmount(payroll.overtimePay()));
            lines.add("Gross salary:" + formatAmount(payroll.grossSalary()));
            lines.add("Insurance:   " + formatAmount(payroll.insuranceAmount()));
            lines.add("Tax:         " + formatAmount(payroll.taxAmount()));
            lines.add("Net salary:  " + formatAmount(payroll.netSalary()));
            lines.add(SEPARATOR);
        }

        lines.add("PAYROLL SUMMARY");
        lines.add("Employee count:   " + report.employees().size());
        lines.add("Total gross:      " + formatAmount(report.totalGross()));

        lines.add("Total deductions: " + formatAmount(report.totalDeductions()));
        
        lines.add("Total net:        " + formatAmount(report.totalNet()));
        lines.add("Average net:      " + formatAmount(report.averageNet()));
        lines.add("Employee with highest net salary: " + report.highestPaidEmployee());
        lines.add(SEPARATOR);
        return String.join("\n", lines) + "\n";
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2).toPlainString();
    }
}
