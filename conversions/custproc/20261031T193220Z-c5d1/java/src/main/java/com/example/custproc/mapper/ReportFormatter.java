package com.example.custproc.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.example.custproc.model.Transaction;

/** Formats the fixed-width REPORT-FILE (133) and ERROR-FILE (80) records. */
@Component
public class ReportFormatter {

    public static final int REPORT_WIDTH = 133;
    public static final int ERROR_WIDTH = 80;
    private static final int EDITED_WIDTH = 11;

    public String header(String yyyymmdd) {
        return fit("1" + fit("BANK TRANSACTION RPT", 20) + " ".repeat(40) + "DATE:" + fit(yyyymmdd, 10), REPORT_WIDTH);
    }

    public String detail(String customerId, String name, String typeText, BigDecimal oldBalance,
            BigDecimal newBalance) {
        String line = "  " + fit(customerId, 10) + "     " + fit(name, 30) + "     " + fit(typeText, 10)
                + "     " + editBalance(oldBalance) + "     " + editBalance(newBalance);
        return fit(line, REPORT_WIDTH);
    }

    public String error(String customerId, String message) {
        return fit(fit(customerId, 10) + ": " + fit(message, 68), ERROR_WIDTH);
    }

    /** Equivalent of PIC ----,--9.99: six digit positions, floating minus, right-justified. */
    public String editBalance(BigDecimal value) {
        BigInteger unscaled = value.setScale(2).unscaledValue();
        BigInteger shown = unscaled.abs().mod(BigInteger.TEN.pow(8));
        String digits = shown.toString();
        while (digits.length() < 3) {
            digits = "0" + digits;
        }
        String integer = digits.substring(0, digits.length() - 2);
        String fraction = digits.substring(digits.length() - 2);
        if (integer.length() > 3) {
            integer = integer.substring(0, integer.length() - 3) + "," + integer.substring(integer.length() - 3);
        }
        String text = (unscaled.signum() < 0 ? "-" : "") + integer + "." + fraction;
        return " ".repeat(EDITED_WIDTH - text.length()) + text;
    }

    public String reportTypeText(Transaction transaction) {
        if (transaction.isDeposit()) {
            return "DEPOSIT";
        }
        if (transaction.isWithdrawal()) {
            return "WITHDRAWAL";
        }
        return "SERVICE FEE";
    }

    /** Alphanumeric MOVE: left-justify, truncate on the right, pad with spaces. */
    public static String fit(String value, int width) {
        if (value.length() >= width) {
            return value.substring(0, width);
        }
        char[] padding = new char[width - value.length()];
        Arrays.fill(padding, ' ');
        return value + new String(padding);
    }
}
