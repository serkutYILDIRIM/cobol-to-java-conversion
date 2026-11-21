package com.example.inventory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;

class InventoryReportFormatterTests {
    private final InventoryReportFormatter formatter = new InventoryReportFormatter();

    @Test
    void preservesAllSourceReportLineWidthsColumnsPaddingAndOrdering() {
        var report = new InventoryService(formatter).run().report();
        var lines = report.lines().toList();
        assertThat(lines).hasSize(46);
        assertThat(lines.get(0)).isEqualTo("-".repeat(78));
        assertThat(lines.get(1)).isEqualTo("TRANSACTION PROCESSING LOG");
        assertThat(lines.get(3)).isEqualTo("APPLIED  TRN: P1001 / S / +00080");
        assertThat(lines.get(5)).isEqualTo("REJECTED TRN: P1003 / S /      25 - "
                + "INSUFFICIENT STOCK" + " ".repeat(12));
        assertThat(lines.get(7)).isEqualTo("APPLIED  TRN: P1005 / A / -00005");
        assertThat(lines.get(15)).isEqualTo(" ");
        assertThat(lines.get(17)).isEqualTo(" ".repeat(25) + "INVENTORY MANAGEMENT REPORT ");
        assertThat(lines.get(19)).hasSize(78);
        assertThat(lines.get(19).substring(0, 29)).isEqualTo("CODE   PRODUCT NAME          ");
        assertThat(lines.subList(21, 31)).allSatisfy(line -> assertThat(line).hasSize(79));
        assertThat(lines.get(21)).isEqualTo("P1001  WIRELESS MOUSE      "
                + "  " + "     40" + "  " + "    50" + "   " + "    24.90"
                + "  " + "        996.00" + "  " + "LOW  ");
        assertThat(lines.get(29).substring(74)).isEqualTo("INACT");
        assertThat(lines.get(35)).isEqualTo("TRANSACTIONS PROCESSED  :" + " ".repeat(5) + "      8");
        assertThat(lines.get(41)).isEqualTo("INVENTORY VALUE (NET)   :" + " ".repeat(5) + "     27,676.07");
        assertThat(lines.get(42)).isEqualTo("TAX (18%)               :" + " ".repeat(5) + "      4,981.69");
        assertThat(lines.get(43)).isEqualTo("INVENTORY VALUE (GROSS) :" + " ".repeat(5) + "     32,657.76");
        assertThat(report).doesNotContain("\r");
        assertThat(report).endsWith("ACTION: PLEASE REVIEW LOW STOCK ITEMS.\n");
    }
}
