package com.example.custproc.model;

import java.math.BigDecimal;
import java.util.List;

/** Outputs of one run: REPORT-FILE lines, ERROR-FILE lines, DISPLAY lines, and totals. */
public record BatchResult(
        List<String> reportLines,
        List<String> errorLines,
        List<String> consoleMessages,
        int recordsRead,
        int recordsProcessed,
        int errorsLogged,
        BigDecimal totalDeposits) {
}
