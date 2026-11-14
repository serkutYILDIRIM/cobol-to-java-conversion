package com.example.inventory.mapper;

import com.example.inventory.dto.InventoryRunResponse.ProductResult;
import com.example.inventory.dto.InventoryRunResponse.Summary;
import com.example.inventory.dto.InventoryRunResponse.TransactionResult;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class InventoryReportFormatter {
    private static final String SEPARATOR = "-".repeat(78);

    public String format(List<ProductResult> products, List<TransactionResult> transactions,
                         Summary summary) {
        var lines = new ArrayList<String>();
        lines.add(SEPARATOR);
        lines.add("TRANSACTION PROCESSING LOG");
        lines.add(SEPARATOR);
        for (TransactionResult transaction : transactions) {
            if (transaction.applied()) {
                lines.add("APPLIED  TRN: " + transaction.code() + " / " + transaction.type()
                        + " / " + String.format(Locale.ROOT, "%+06d", transaction.quantity()));
            } else {
                String quantity = (transaction.quantity() < 0 ? "-" : " ")
                        + integer(Math.abs(transaction.quantity()), 6);
                lines.add(text("REJECTED TRN: ", 14) + transaction.code() + " / "
                        + transaction.type() + " / " + quantity + " - "
                        + text(transaction.rejectionReason(), 30));
            }
        }
        lines.add(" ");
        lines.add(SEPARATOR);
        lines.add(" ".repeat(25) + text("INVENTORY MANAGEMENT REPORT", 28));
        lines.add(SEPARATOR);
        lines.add(text("CODE", 7) + text("PRODUCT NAME", 22) + text("QTY", 9)
                + text("REORDER", 9) + text("UNIT PRICE", 12)
                + text("STOCK VALUE", 13) + text("FLAG", 6));
        lines.add(SEPARATOR);
        for (ProductResult product : products) {
            lines.add(text(product.code(), 5) + "  " + text(product.name(), 20) + "  "
                    + integer(product.quantity(), 7) + "  " + integer(product.reorderLevel(), 6)
                    + "   " + decimal(product.unitPrice(), 9) + "  "
                    + decimal(product.stockValue(), 14) + "  " + text(product.flag(), 5));
        }
        lines.add(SEPARATOR);
        lines.add(" ");
        lines.add("SUMMARY");
        lines.add(SEPARATOR);
        lines.add(count("TRANSACTIONS PROCESSED  :", summary.processed()));
        lines.add(count("TRANSACTIONS REJECTED   :", summary.rejected()));
        lines.add(count("TOTAL UNITS RECEIVED    :", summary.totalReceived()));
        lines.add(count("TOTAL UNITS SOLD        :", summary.totalSold()));
        lines.add(count("LOW STOCK PRODUCTS      :", summary.lowStockCount()));
        lines.add(SEPARATOR);
        lines.add(money("INVENTORY VALUE (NET)   :", summary.netValue()));
        lines.add(money("TAX (18%)               :", summary.taxAmount()));
        lines.add(money("INVENTORY VALUE (GROSS) :", summary.grossValue()));
        lines.add(SEPARATOR);
        lines.add(summary.lowStockCount() > 0 ? "ACTION: PLEASE REVIEW LOW STOCK ITEMS."
                : "ALL PRODUCTS ARE ABOVE REORDER LEVEL.");
        return String.join("\n", lines) + "\n";
    }

    private static String count(String label, int value) {
        return text(label, 30) + integer(value % 1_000_000, 7);
    }

    private static String money(String label, BigDecimal value) {
        return text(label, 30) + decimal(value, 14);
    }

    private static String integer(int value, int width) {
        return number(BigDecimal.valueOf(value), "#,##0", width);
    }

    private static String decimal(BigDecimal value, int width) {
        return number(value, "#,##0.00", width);
    }

    private static String number(BigDecimal value, String picture, int width) {
        var format = new DecimalFormat(picture, DecimalFormatSymbols.getInstance(Locale.US));
        format.setRoundingMode(RoundingMode.UNNECESSARY);
        String result = format.format(value);
        return " ".repeat(Math.max(0, width - result.length())) + result;
    }

    private static String text(String value, int width) {
        return value.length() >= width ? value.substring(0, width)
                : value + " ".repeat(width - value.length());
    }
}
