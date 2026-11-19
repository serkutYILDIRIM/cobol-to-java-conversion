package com.example.inventory.service;

import com.example.inventory.dto.InventoryRunResponse;
import com.example.inventory.dto.InventoryRunResponse.ProductResult;
import com.example.inventory.dto.InventoryRunResponse.Summary;
import com.example.inventory.dto.InventoryRunResponse.TransactionResult;
import com.example.inventory.mapper.InventoryReportFormatter;
import com.example.inventory.model.EmbeddedInventory;
import com.example.inventory.model.Product;
import com.example.inventory.model.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");
    private static final BigDecimal AMOUNT_MODULUS = new BigDecimal("1000000000");
    private final InventoryReportFormatter formatter;

    public InventoryService(InventoryReportFormatter formatter) {
        this.formatter = formatter;
    }

    public InventoryRunResponse run() {
        return process(EmbeddedInventory.products(), EmbeddedInventory.transactions());
    }

    InventoryRunResponse process(List<Product> initialProducts, List<Transaction> transactions) {
        var products = new ArrayList<>(initialProducts);
        var outcomes = new ArrayList<TransactionResult>();
        int processed = 0;
        int rejected = 0;
        int received = 0;
        int sold = 0;
        for (Transaction transaction : transactions) {
            int index = findProduct(products, transaction.code());
            String reason = index < 0 ? "PRODUCT NOT FOUND"
                    : rejectionReason(products.get(index), transaction);
            if (!reason.isEmpty()) {
                rejected = unsigned(rejected + 1L, 1000);
                outcomes.add(new TransactionResult(transaction.code(), transaction.type(),
                        transaction.quantity(), false, reason));
                continue;
            }
            Product product = products.get(index);
            long delta = transaction.quantity();
            switch (transaction.type()) {
                case "R" -> received = unsigned(received + delta, 10_000_000);
                case "S" -> {
                    sold = unsigned(sold + delta, 10_000_000);
                    delta = -delta;
                }
                case "A" -> { }
                default -> throw new IllegalStateException("Validated transaction type is unsupported");
            }
            products.set(index, product.withQuantity(unsigned(product.quantity() + delta, 100_000)));
            processed = unsigned(processed + 1L, 1000);
            outcomes.add(new TransactionResult(transaction.code(), transaction.type(),
                    transaction.quantity(), true, ""));
        }

        var details = new ArrayList<ProductResult>();
        BigDecimal net = new BigDecimal("0.00");
        int lowStock = 0;
        for (Product product : products) {
            BigDecimal value = amount(product.unitPrice().multiply(BigDecimal.valueOf(product.quantity())));
            net = amount(net.add(value));
            String flag;
            if ("I".equals(product.status())) {
                flag = "INACT";
            } else if (product.quantity() <= product.reorderLevel()) {
                flag = "LOW";
                lowStock = unsigned(lowStock + 1L, 1000);
            } else {
                flag = "OK";
            }
            details.add(new ProductResult(product.code(), product.name(), product.quantity(),
                    product.reorderLevel(), product.unitPrice(), product.status(), value, flag));
        }
        BigDecimal tax = amount(net.multiply(TAX_RATE));
        var summary = new Summary(processed, rejected, received, sold, lowStock,
                net, tax, amount(net.add(tax)));
        return new InventoryRunResponse(details, outcomes, summary,
                formatter.format(details, outcomes, summary));
    }

    private static int findProduct(List<Product> products, String code) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).code().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    private static String rejectionReason(Product product, Transaction transaction) {
        if ("I".equals(product.status())) {
            return "PRODUCT IS INACTIVE";
        }
        if (!List.of("R", "S", "A").contains(transaction.type())) {
            return "INVALID TRANSACTION TYPE";
        }
        if ("S".equals(transaction.type()) && transaction.quantity() > product.quantity()) {
            return "INSUFFICIENT STOCK";
        }
        return "";
    }

    private static int unsigned(long value, int modulus) {
        // Unsigned DISPLAY receivers discard the sign and excess high-order digits.
        return (int) (Math.abs(value) % modulus);
    }
}
