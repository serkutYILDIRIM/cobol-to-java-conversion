package com.example.salaryreport.controller;

import com.example.salaryreport.service.SalaryReportService;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/salary-reports")
public class SalaryReportController {

    private static final MediaType UTF8_TEXT = new MediaType("text", "plain", StandardCharsets.UTF_8);

    private final SalaryReportService service;

    public SalaryReportController(SalaryReportService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = "text/plain;charset=UTF-8")
    public ResponseEntity<byte[]> generate(@RequestBody(required = false) byte[] input) {
        byte[] employeeData = input == null ? new byte[0] : input;
        byte[] report = service.generate(new ByteArrayInputStream(employeeData)).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(UTF8_TEXT)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("EMP_REPORT.TXT").build().toString())
                .body(report);
    }
}
