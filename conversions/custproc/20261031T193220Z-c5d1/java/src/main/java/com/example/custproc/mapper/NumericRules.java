package com.example.custproc.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;

/** COBOL numeric semantics: silent high-order truncation when no ON SIZE ERROR is coded. */
public final class NumericRules {

    private NumericRules() {
    }

    /** Keeps the sign and the low-order {@code integerDigits} integer digits, scale 2. */
    public static BigDecimal truncate(BigDecimal value, int integerDigits) {
        BigInteger unscaled = value.setScale(2).unscaledValue();
        BigInteger modulus = BigInteger.TEN.pow(integerDigits + 2);
        BigInteger magnitude = unscaled.abs().mod(modulus);
        BigInteger signed = unscaled.signum() < 0 ? magnitude.negate() : magnitude;
        return new BigDecimal(signed, 2);
    }

    /** PIC 9(5) COMP counter increment with decimal truncation. */
    public static int increment(int counter) {
        return (counter + 1) % 100_000;
    }
}
