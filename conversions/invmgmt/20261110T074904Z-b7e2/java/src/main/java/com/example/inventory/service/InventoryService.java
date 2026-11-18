package com.example.inventory.service;


import org.springframework.stereotype.Service;

import com.example.inventory.mapper.InventoryReportFormatter;

@Service
public class InventoryService {
    private final InventoryReportFormatter formatter;

    public InventoryService(InventoryReportFormatter formatter) {
        this.formatter = formatter;
    }
}
