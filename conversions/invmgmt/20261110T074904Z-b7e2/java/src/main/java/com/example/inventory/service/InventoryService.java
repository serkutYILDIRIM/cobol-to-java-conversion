package com.example.inventory.service;

import org.springframework.stereotype.Service;

import com.example.inventory.dto.InventoryRunResponse;
import com.example.inventory.mapper.InventoryReportFormatter;
import com.example.inventory.model.EmbeddedInventory;

@Service
public class InventoryService {

    private final InventoryReportFormatter formatter;

    public InventoryService(InventoryReportFormatter formatter) {
        this.formatter = formatter;
    }

    public InventoryRunResponse run() {
        return process(EmbeddedInventory.products(), EmbeddedInventory.transactions());
    }
}
