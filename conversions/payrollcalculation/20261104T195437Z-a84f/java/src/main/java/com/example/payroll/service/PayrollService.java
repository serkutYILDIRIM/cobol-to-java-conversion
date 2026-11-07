package com.example.payroll.service;

import com.example.payroll.model.Employee;
import com.example.payroll.model.EmployeePayroll;
import com.example.payroll.model.PayrollReport;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PayrollService {

    private static final int MONTHLY_WORK_HOURS = 160;
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.50");
    private static final BigDecimal INSURANCE_RATE = new BigDecimal("0.14");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.15");
    private static final BigDecimal ZERO_MONEY = new BigDecimal("0.00");

    public PayrollReport generateReport() {
        List<EmployeePayroll> payrolls = sampleEmployees().stream()
                .map(this::calculateSalary)
                .toList();

        BigDecimal totalGross = ZERO_MONEY;
        BigDecimal totalDeductions = ZERO_MONEY;
        BigDecimal totalNet = ZERO_MONEY;
        EmployeePayroll highestPaid = payrolls.getFirst();

        for (EmployeePayroll payroll : payrolls) {
            totalGross = totalGross.add(payroll.grossSalary());
            totalDeductions = totalDeductions
                    .add(payroll.insuranceAmount())
                    .add(payroll.taxAmount());
            totalNet = totalNet.add(payroll.netSalary());
            if (payroll.netSalary().compareTo(highestPaid.netSalary()) > 0) {
                highestPaid = payroll;
            }
        }

        BigDecimal averageNet = totalNet.divide(
                BigDecimal.valueOf(payrolls.size()), 2, RoundingMode.HALF_UP);

        return new PayrollReport(
                payrolls,
                totalGross,
                totalDeductions,
                totalNet,
                averageNet,
                highestPaid.employee().name());
    }

    private EmployeePayroll calculateSalary(Employee employee) {
        BigDecimal bonusRate = switch (employee.performanceScore()) {
            case 5 -> new BigDecimal("0.15");
            case 4 -> new BigDecimal("0.10");
            default -> BigDecimal.ZERO;
        };
        BigDecimal bonus = employee.baseSalary()
                .multiply(bonusRate)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal overtime = employee.baseSalary()
                .multiply(BigDecimal.valueOf(employee.overtimeHours()))
                .multiply(OVERTIME_MULTIPLIER)
                .divide(BigDecimal.valueOf(MONTHLY_WORK_HOURS), 2, RoundingMode.HALF_UP);
        BigDecimal gross = employee.baseSalary().add(bonus).add(overtime);
        BigDecimal insurance = gross.multiply(INSURANCE_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxableIncome = gross.subtract(insurance);
        BigDecimal tax = taxableIncome.multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal net = taxableIncome.subtract(tax);

        return new EmployeePayroll(employee, bonus, overtime, gross, insurance, tax, net);
    }

    private List<Employee> sampleEmployees() {
        return List.of(
                new Employee(1001, "Sample Employee A", "Software",
                        new BigDecimal("45000.00"), 8, 5),
                new Employee(1002, "Sample Employee B", "Accounting",
                        new BigDecimal("38000.00"), 4, 4),
                new Employee(1003, "Sample Employee C", "Operations",
                        new BigDecimal("32000.00"), 0, 3));
    }
}
