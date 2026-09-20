package com.example.salaryreport.service;

import com.example.salaryreport.model.Employee;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class SalaryReportFormatter {

    private static final DateTimeFormatter REPORT_DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public String heading(LocalDate date, int pageNumber) {
        return reportRecord(
                fixed("DATE:", 6)
                        + REPORT_DATE.format(date)
                        + " ".repeat(15)
                        + fixed("EMPLOYEE SALARY REPORT", 24)
                        + " ".repeat(15)
                        + fixed("PAGE:", 6)
                        + leftPad(Integer.toString(pageNumber), 4));
    }

    public String equalsSeparator() {
        return "=".repeat(80);
    }

    public String columnHeading() {
        return reportRecord(
                fixed("ID", 7)
                        + fixed("FULL NAME", 32)
                        + fixed("DEPARTMENT", 12)
                        + fixed("GROSS SALARY", 15));
    }

    public String dashSeparator() {
        return "-".repeat(80);
    }

    public String detail(Employee employee) {
        String fullName = delimited(employee.firstName()) + " " + delimited(employee.lastName());
        return reportRecord(
                fixed(employee.id(), 5)
                        + "  "
                        + fixed(fullName, 30)
                        + "  "
                        + fixed(employee.department(), 10)
                        + "  "
                        + money(employee.salary(), 10)
                        + " ".repeat(17));
    }

    public String employeeTotal(int count) {
        return reportRecord(
                fixed("TOTAL EMPLOYEES    :", 20)
                        + leftPad(String.format(Locale.ROOT, "%,d", count), 6)
                        + " ".repeat(55));
    }

    public String salaryTotal(BigDecimal salary) {
        return reportRecord(fixed("TOTAL SALARY PAID  :", 20) + money(salary, 14) + " ".repeat(45));
    }

    public String emptyInput() {
        return reportRecord("NO RECORDS FOUND TO PROCESS.");
    }

    private String delimited(String value) {
        int delimiter = value.indexOf("  ");
        return delimiter >= 0 ? value.substring(0, delimiter) : value;
    }

    private String money(BigDecimal value, int numericWidth) {
        DecimalFormat format = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
        format.setParseBigDecimal(true);
        return "$" + leftPad(format.format(value), numericWidth);
    }

    private String reportRecord(String value) {
        return fixed(value, 80);
    }

    private String fixed(String value, int width) {
        int codePoints = value.codePointCount(0, value.length());
        if (codePoints > width) {
            return value.substring(0, value.offsetByCodePoints(0, width));
        }
        return value + " ".repeat(width - codePoints);
    }

    private String leftPad(String value, int width) {
        return " ".repeat(Math.max(0, width - value.length())) + value;
    }
}
