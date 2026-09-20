package com.example.salaryreport.service;

import com.example.salaryreport.dao.EmployeeRecordReader;
import com.example.salaryreport.exception.NumericCapacityException;
import com.example.salaryreport.model.Employee;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SalaryReportService {

    private static final int LINE_LIMIT = 40;
    private static final int MAX_PAGE_COUNT = 999;
    private static final int MAX_EMPLOYEE_COUNT = 99_999;
    private static final BigDecimal MAX_TOTAL_SALARY = new BigDecimal("999999999.99");

    private final EmployeeRecordReader reader;
    private final SalaryReportFormatter formatter;
    private final Clock clock;

    public SalaryReportService(EmployeeRecordReader reader, SalaryReportFormatter formatter, Clock clock) {
        this.reader = reader;
        this.formatter = formatter;
        this.clock = clock;
    }

    public String generate(InputStream input) {
        List<Employee> employees = reader.read(input);
        LocalDate reportDate = LocalDate.now(clock);
        if (employees.isEmpty()) {
            return formatter.emptyInput() + "\n";
        }

        StringBuilder report = new StringBuilder();
        int pageCount = 0;
        int lineCount = 99;
        int employeeCount = 0;
        BigDecimal totalSalary = BigDecimal.ZERO.setScale(2);

        for (Employee employee : employees) {
            if (lineCount > LINE_LIMIT) {
                pageCount = nextPage(pageCount);
                if (pageCount > 1) {
                    report.append('\f').append('\n');
                }
                appendLine(report, formatter.heading(reportDate, pageCount));
                appendLine(report, formatter.equalsSeparator());
                appendLine(report, formatter.columnHeading());
                appendLine(report, formatter.dashSeparator());
                lineCount = 5;
            }

            appendLine(report, formatter.detail(employee));
            lineCount++;
            employeeCount = nextEmployeeCount(employeeCount);
            totalSalary = addSalary(totalSalary, employee.salary());
        }

        appendLine(report, formatter.dashSeparator());
        appendLine(report, formatter.employeeTotal(employeeCount));
        appendLine(report, formatter.salaryTotal(totalSalary));
        return report.toString();
    }

    private int nextPage(int pageCount) {
        if (pageCount == MAX_PAGE_COUNT) {
            throw new NumericCapacityException("Page count exceeds the COBOL PIC 9(03) capacity of 999.");
        }
        return pageCount + 1;
    }

    private int nextEmployeeCount(int employeeCount) {
        if (employeeCount == MAX_EMPLOYEE_COUNT) {
            throw new NumericCapacityException(
                    "Employee count exceeds the COBOL PIC 9(05) capacity of 99,999.");
        }
        return employeeCount + 1;
    }

    private BigDecimal addSalary(BigDecimal total, BigDecimal salary) {
        BigDecimal result = total.add(salary);
        if (result.compareTo(MAX_TOTAL_SALARY) > 0) {
            throw new NumericCapacityException(
                    "Total salary exceeds the COBOL PIC 9(09)V99 capacity of 999,999,999.99.");
        }
        return result;
    }

    private void appendLine(StringBuilder report, String line) {
        report.append(line).append('\n');
    }
}
