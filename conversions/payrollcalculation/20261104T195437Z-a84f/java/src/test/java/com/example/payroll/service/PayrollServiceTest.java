package com.example.payroll.service;

import com.example.payroll.model.EmployeePayroll;
import com.example.payroll.model.PayrollReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PayrollServiceTest {

    private final PayrollService payrollService = new PayrollService();

    @Test
    void calculatesSampleEmployeeAmountsAndSummary() {
        PayrollReport report = payrollService.generateReport();

        assertThat(report.employees()).hasSize(3);
        assertEmployee(report.employees().get(0), "6750.00", "3375.00",
                "55125.00", "7717.50", "7111.13", "40296.37");
        assertEmployee(report.employees().get(1), "3800.00", "1425.00",
                "43225.00", "6051.50", "5576.03", "31597.47");
        assertEmployee(report.employees().get(2), "0.00", "0.00",
                "32000.00", "4480.00", "4128.00", "23392.00");

        assertMoney(report.totalGross(), "130350.00");
        assertMoney(report.totalDeductions(), "35064.16");
        assertMoney(report.totalNet(), "95285.84");
        assertMoney(report.averageNet(), "31761.95");
        assertThat(report.highestPaidEmployee()).isEqualTo("Sample Employee A");
    }

    @Test
    void keepsReportStateIndependentBetweenCalls() {
        PayrollReport first = payrollService.generateReport();
        PayrollReport second = payrollService.generateReport();

        assertThat(second).isEqualTo(first);
        assertThat(second.employees()).isNotSameAs(first.employees());
    }

    private void assertEmployee(
            EmployeePayroll payroll,
            String bonus,
            String overtime,
            String gross,
            String insurance,
            String tax,
            String net) {
        assertMoney(payroll.bonusAmount(), bonus);
        assertMoney(payroll.overtimePay(), overtime);
        assertMoney(payroll.grossSalary(), gross);
        assertMoney(payroll.insuranceAmount(), insurance);
        assertMoney(payroll.taxAmount(), tax);
        assertMoney(payroll.netSalary(), net);
    }
}
