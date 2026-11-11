package com.example.inventory.controller;

import com.example.inventory.dto.InventoryRunResponse;
import com.example.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    //
    @PostMapping("/runs")
    public InventoryRunResponse run() {
        return service.run();
    }
}
