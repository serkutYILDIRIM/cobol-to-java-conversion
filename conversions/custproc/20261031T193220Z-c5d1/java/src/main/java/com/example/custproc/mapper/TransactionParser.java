package com.example.custproc.mapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.custproc.exception.InvalidTransactionFileException;
import com.example.custproc.model.Transaction;

/** Parses 80-character fixed-width TRANS-REC lines; text is not trimmed. */
@Component
public class TransactionParser {

    static final int RECORD_LENGTH = 80;

    public List<Transaction> parse(InputStream input) throws IOException {
        String content = new String(input.readAllBytes(), StandardCharsets.ISO_8859_1);
        List<Transaction> result = new ArrayList<>();
        if (content.isEmpty()) {
            return result;
        }
        String[] lines = content.split("\n", -1);
        int count = lines.length;
        if (lines[count - 1].isEmpty()) {
            count--;
        }
        for (int i = 0; i < count; i++) {
            String line = lines[i].endsWith("\r") ? lines[i].substring(0, lines[i].length() - 1) : lines[i];
            result.add(parseRecord(line, i + 1));
        }
        return result;
    }

    public List<Transaction> parse(byte[] bytes) throws IOException {
        return parse(new ByteArrayInputStream(bytes));
    }

    private Transaction parseRecord(String line, int number) {
        if (line.length() != RECORD_LENGTH) {
            throw new InvalidTransactionFileException(
                    "Record " + number + " must be exactly " + RECORD_LENGTH + " characters.");
        }
        String amountText = line.substring(11, 20);
        for (int i = 0; i < amountText.length(); i++) {
            if (amountText.charAt(i) < '0' || amountText.charAt(i) > '9') {
                throw new InvalidTransactionFileException(
                        "Record " + number + " has a non-numeric amount in columns 12-20.");
            }
        }
        return new Transaction(line.substring(0, 10), line.charAt(10),
                new BigDecimal(new BigInteger(amountText), 2));
    }
}
