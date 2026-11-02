package com.example.custproc.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;

/** PIC S9(9)V99 COMP-3: 11 digits plus a sign nibble in 6 bytes (C positive, D negative). */
public final class PackedDecimal {

    public static final int LENGTH = 6;

    private PackedDecimal() {
    }

    public static byte[] encode(BigDecimal value) {
        BigInteger unscaled = value.setScale(2).unscaledValue();
        String digits = String.format("%011d", unscaled.abs());
        if (digits.length() != 11) {
            throw new IllegalArgumentException("Value does not fit S9(9)V99");
        }
        byte[] out = new byte[LENGTH];
        for (int i = 0; i < 5; i++) {
            out[i] = (byte) (((digits.charAt(2 * i) - '0') << 4) | (digits.charAt(2 * i + 1) - '0'));
        }
        int sign = unscaled.signum() < 0 ? 0x0D : 0x0C;
        out[5] = (byte) (((digits.charAt(10) - '0') << 4) | sign);
        return out;
    }

    public static BigDecimal decode(byte[] data) {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < LENGTH - 1; i++) {
            digits.append(nibble(data[i] >> 4 & 0x0F)).append(nibble(data[i] & 0x0F));
        }
        digits.append(nibble(data[LENGTH - 1] >> 4 & 0x0F));
        int sign = data[LENGTH - 1] & 0x0F;
        if (sign != 0x0C && sign != 0x0D && sign != 0x0F) {
            throw new IllegalArgumentException("Invalid packed-decimal sign nibble");
        }
        BigInteger magnitude = new BigInteger(digits.toString());
        return new BigDecimal(sign == 0x0D ? magnitude.negate() : magnitude, 2);
    }

    private static char nibble(int value) {
        if (value > 9) {
            throw new IllegalArgumentException("Invalid packed-decimal digit");
        }
        return (char) ('0' + value);
    }
}
