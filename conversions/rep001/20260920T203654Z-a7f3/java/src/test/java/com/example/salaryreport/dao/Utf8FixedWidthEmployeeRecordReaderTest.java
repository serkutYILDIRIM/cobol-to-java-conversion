package com.example.salaryreport.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.salaryreport.TestRecords;
import com.example.salaryreport.exception.InvalidEmployeeDataException;
import com.example.salaryreport.exception.ReportIoException;
import com.example.salaryreport.mapper.EmployeeRecordMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class Utf8FixedWidthEmployeeRecordReaderTest {

    private final Utf8FixedWidthEmployeeRecordReader reader =
            new Utf8FixedWidthEmployeeRecordReader(new EmployeeRecordMapper());

    @Test
    void readsLfCrLfAndFinalLineWithoutTerminator() {
        String first = TestRecords.employee("00001", "ALICE", "SMITH", "FINANCE", "00123456");
        String second = TestRecords.employee("00002", "BOB", "JONES", "SALES", "00001000");
        String third = TestRecords.employee("00003", "CARA", "BROWN", "HR", "00002000");
        byte[] input = (first + "\r\n" + second + "\n" + third).getBytes(StandardCharsets.UTF_8);

        var employees = reader.read(new ByteArrayInputStream(input));

        assertThat(employees).extracting(employee -> employee.id().trim())
                .containsExactly("00001", "00002", "00003");
    }

    @Test
    void acceptsEmptyInput() {
        assertThat(reader.read(new ByteArrayInputStream(new byte[0]))).isEmpty();
    }

    @Test
    void rejectsMalformedUtf8() {
        assertThatThrownBy(() -> reader.read(new ByteArrayInputStream(new byte[] {(byte) 0xc3, 0x28})))
                .isInstanceOf(InvalidEmployeeDataException.class)
                .hasMessage("Employee data must be valid UTF-8.");
    }

    @Test
    void rejectsBareCarriageReturnAsALineEnding() {
        String record = TestRecords.employee("00001", "A", "B", "D", "00000100") + "\r";

        assertThatThrownBy(() -> reader.read(
                        new ByteArrayInputStream(record.getBytes(StandardCharsets.UTF_8))))
                .isInstanceOf(InvalidEmployeeDataException.class)
                .hasMessage("Record 1 contains an unsupported carriage return.");
    }

    @Test
    void translatesReadFailures() {
        InputStream failingInput = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("test failure");
            }
        };

        assertThatThrownBy(() -> reader.read(failingInput))
                .isInstanceOf(ReportIoException.class)
                .hasMessage("Failed to read employee data.");
    }
}
