package com.example.payroll.controller;

import com.example.payroll.dto.PayrollReportResponse;
import com.example.payroll.mapper.PayrollReportFormatter;
import com.example.payroll.service.PayrollService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payroll")
public class PayrollReportController {

    private final PayrollService payrollService;
    private final PayrollReportFormatter reportFormatter;

    public PayrollReportController(
            PayrollService payrollService,
            PayrollReportFormatter reportFormatter) {
        this.payrollService = payrollService;
        this.reportFormatter = reportFormatter;
    }

    @GetMapping("/report")
    public PayrollReportResponse report() {
        return new PayrollReportResponse(
                reportFormatter.format(payrollService.generateReport()));
    }
}
