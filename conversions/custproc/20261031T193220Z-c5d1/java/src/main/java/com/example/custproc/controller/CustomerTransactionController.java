package com.example.custproc.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.custproc.dto.BatchResultResponse;
import com.example.custproc.mapper.TransactionParser;
import com.example.custproc.model.BatchResult;
import com.example.custproc.service.CustomerTransactionService;

@RestController
@RequestMapping("/api/v1/customer-transactions")
public class CustomerTransactionController {

    private final CustomerTransactionService service;
    private final TransactionParser parser;

    public CustomerTransactionController(CustomerTransactionService service, TransactionParser parser) {
        this.service = service;
        this.parser = parser;
    }

    @PostMapping(path = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BatchResultResponse processBatch(@RequestPart("file") MultipartFile file) throws IOException {
        BatchResult result = service.process(parser.parse(file.getInputStream()));
        return new BatchResultResponse(result.reportLines(), result.errorLines(), result.consoleMessages(),
                result.recordsRead(), result.recordsProcessed(), result.errorsLogged(), result.totalDeposits());
    }
}
