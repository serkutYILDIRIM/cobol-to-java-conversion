package com.example.salaryreport.mapper;

import com.example.salaryreport.exception.InvalidEmployeeDataException;
import com.example.salaryreport.model.Employee;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRecordMapper {

    private static final int RECORD_WIDTH = 80;

    public Employee map(String record, int lineNumber) {
        int actualWidth = record.codePointCount(0, record.length());
        if (actualWidth != RECORD_WIDTH) {
            throw new InvalidEmployeeDataException(
                    "Record %d must contain exactly 80 characters; found %d."
                            .formatted(lineNumber, actualWidth));
        }

        String salaryDigits = slice(record, 45, 53);
        if (!salaryDigits.matches("[0-9]{8}")) {
            throw new InvalidEmployeeDataException(
                    "Record %d salary must contain exactly eight digits.".formatted(lineNumber));
        }

        return new Employee(
                slice(record, 0, 5),
                slice(record, 5, 20),
                slice(record, 20, 35),
                slice(record, 35, 45),
                new BigDecimal(salaryDigits).movePointLeft(2));
    }

    private String slice(String value, int startCodePoint, int endCodePoint) {
        int start = value.offsetByCodePoints(0, startCodePoint);
        int end = value.offsetByCodePoints(0, endCodePoint);
        return value.substring(start, end);
    }
}
