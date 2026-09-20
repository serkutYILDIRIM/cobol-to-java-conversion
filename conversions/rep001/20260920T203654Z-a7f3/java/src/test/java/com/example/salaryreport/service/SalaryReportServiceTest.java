package com.example.salaryreport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.salaryreport.TestRecords;
import com.example.salaryreport.dao.Utf8FixedWidthEmployeeRecordReader;
import com.example.salaryreport.exception.NumericCapacityException;
import com.example.salaryreport.mapper.EmployeeRecordMapper;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class SalaryReportServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-09-20T12:00:00Z"), ZoneOffset.UTC);

    private final SalaryReportService service = new SalaryReportService(
            new Utf8FixedWidthEmployeeRecordReader(new EmployeeRecordMapper()),
            new SalaryReportFormatter(),
            FIXED_CLOCK);

    @Test
    void createsFixedWidthReportWithEnglishHeadingsAndTotals() {
        String input = TestRecords.employee("00001", "ALICE", "SMITH", "FINANCE", "00123456");

        String report = generate(input);
        String[] lines = report.split("\n", -1);

        assertThat(lines).hasSize(9);
        assertThat(lines[0]).startsWith("DATE: 20/09/2026").contains("EMPLOYEE SALARY REPORT").endsWith("PAGE:    1");
        assertThat(lines[2]).startsWith("ID     FULL NAME").contains("DEPARTMENT").contains("GROSS SALARY");
        assertThat(lines[4]).startsWith("00001  ALICE SMITH").contains("FINANCE").contains("$  1,234.56");
        assertThat(lines[6]).startsWith("TOTAL EMPLOYEES    :     1");
        assertThat(lines[7]).startsWith("TOTAL SALARY PAID  :$      1,234.56");
        assertThat(List.of(lines).subList(0, 8))
                .allSatisfy(line -> assertThat(line.codePointCount(0, line.length())).isEqualTo(80));
    }

    @Test
    void returnsSinglePaddedLineForEmptyInput() {
        String report = generate("");

        assertThat(report).startsWith("NO RECORDS FOUND TO PROCESS.").endsWith(" \n");
        assertThat(report.substring(0, report.length() - 1)).hasSize(80);
    }

    @Test
    void preservesExactDoubleSpaceNameDelimiterAndClearsEveryName() {
        String first = TestRecords.employee("00001", "ALEXANDER  OLD", "LONGSURNAME", "OPS", "00000100");
        String second = TestRecords.employee("00002", "BO", "LI", "HR", "00000100");

        String[] lines = generate(first + "\n" + second).split("\n");

        assertThat(lines[4]).startsWith("00001  ALEXANDER LONGSURNAME");
        assertThat(lines[5]).startsWith("00002  BO LI");
        assertThat(lines[5].substring(7, 37).trim()).isEqualTo("BO LI");
    }

    @Test
    void startsSecondPageAfterThirtySixDetailsWithStandaloneFormFeed() {
        StringBuilder input = new StringBuilder();
        for (int index = 1; index <= 37; index++) {
            if (index > 1) {
                input.append('\n');
            }
            input.append(TestRecords.employee("%05d".formatted(index), "NAME", "LAST", "DEPT", "00000100"));
        }

        String[] lines = generate(input.toString()).split("\n", -1);

        assertThat(lines[40]).isEqualTo("\f");
        assertThat(lines[41]).endsWith("PAGE:    2");
        assertThat(lines[45]).startsWith("00037");
    }

    @Test
    void rejectsSalaryTotalBeyondCobolCapacity() {
        String record = TestRecords.employee("00001", "A", "B", "D", "99999999");
        String input = String.join("\n", java.util.Collections.nCopies(1001, record));

        assertThatThrownBy(() -> generate(input))
                .isInstanceOf(NumericCapacityException.class)
                .hasMessageContaining("999,999,999.99");
    }

    @Test
    void concurrentCallsDoNotShareWorkingState() throws Exception {
        String input = TestRecords.employee("00001", "ALICE", "SMITH", "FINANCE", "00000100");
        String expected = generate(input);
        List<Callable<String>> calls = new ArrayList<>();
        for (int index = 0; index < 20; index++) {
            calls.add(() -> generate(input));
        }

        try (var executor = Executors.newFixedThreadPool(8)) {
            assertThat(executor.invokeAll(calls))
                    .allSatisfy(result -> assertThat(result.get()).isEqualTo(expected));
        }
    }

    private String generate(String input) {
        return service.generate(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}
