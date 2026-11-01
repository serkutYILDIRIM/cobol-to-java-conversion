package com.example.custproc.dto;

import java.math.BigDecimal;
import java.util.List;

public record BatchResultResponse(
        List<String> reportLines,
        List<String> errorLines,
        List<String> consoleMessages,
        int recordsRead,
        int recordsProcessed,
        int errorsLogged,
        BigDecimal totalDeposits) {
}
