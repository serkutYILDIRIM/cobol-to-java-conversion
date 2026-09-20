package com.example.salaryreport.dao;

import com.example.salaryreport.exception.InvalidEmployeeDataException;
import com.example.salaryreport.exception.ReportIoException;
import com.example.salaryreport.mapper.EmployeeRecordMapper;
import com.example.salaryreport.model.Employee;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class Utf8FixedWidthEmployeeRecordReader implements EmployeeRecordReader {

    private final EmployeeRecordMapper mapper;

    public Utf8FixedWidthEmployeeRecordReader(EmployeeRecordMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Employee> read(InputStream input) {
        try (input) {
            return mapRecords(decodeStrictUtf8(input.readAllBytes()));
        } catch (InvalidEmployeeDataException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new ReportIoException("Failed to read employee data.", exception);
        }
    }

    private String decodeStrictUtf8(byte[] input) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(input))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new InvalidEmployeeDataException("Employee data must be valid UTF-8.", exception);
        }
    }

    private List<Employee> mapRecords(String content) {
        if (content.isEmpty()) {
            return List.of();
        }

        List<Employee> employees = new ArrayList<>();
        int recordStart = 0;
        int lineNumber = 1;
        for (int index = 0; index < content.length(); index++) {
            if (content.charAt(index) == '\n') {
                employees.add(mapLine(content.substring(recordStart, index), lineNumber++, true));
                recordStart = index + 1;
            }
        }
        if (recordStart < content.length()) {
            employees.add(mapLine(content.substring(recordStart), lineNumber, false));
        }
        return List.copyOf(employees);
    }

    private Employee mapLine(String physicalLine, int lineNumber, boolean terminatedByLineFeed) {
        String record = terminatedByLineFeed && physicalLine.endsWith("\r")
                ? physicalLine.substring(0, physicalLine.length() - 1)
                : physicalLine;
        if (record.indexOf('\r') >= 0) {
            throw new InvalidEmployeeDataException(
                    "Record %d contains an unsupported carriage return.".formatted(lineNumber));
        }
        return mapper.map(record, lineNumber);
    }
}
