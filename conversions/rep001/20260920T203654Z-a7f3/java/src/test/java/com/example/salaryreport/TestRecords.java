package com.example.salaryreport;

public final class TestRecords {

    private TestRecords() {
    }

    public static String employee(String id, String firstName, String lastName, String department, String salaryDigits) {
        return fixed(id, 5)
                + fixed(firstName, 15)
                + fixed(lastName, 15)
                + fixed(department, 10)
                + fixed(salaryDigits, 8)
                + " ".repeat(27);
    }

    private static String fixed(String value, int width) {
        int codePoints = value.codePointCount(0, value.length());
        if (codePoints > width) {
            return value.substring(0, value.offsetByCodePoints(0, width));
        }
        return value + " ".repeat(width - codePoints);
    }
}
