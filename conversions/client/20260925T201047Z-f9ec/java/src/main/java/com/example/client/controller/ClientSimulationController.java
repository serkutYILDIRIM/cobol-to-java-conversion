package com.example.client.controller;

import com.example.client.dto.ClientSettingsRequest;
import com.example.client.dto.SimulationResponse;
import com.example.client.service.ClientSimulationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client-settings/simulations")
public final class ClientSimulationController {

    private final ClientSimulationService service;

    public ClientSimulationController(ClientSimulationService service) {
        this.service = service;
    }

    @PostMapping

    public SimulationResponse simulate(@Valid @RequestBody ClientSettingsRequest request) {
        return SimulationResponse.from(service.simulate(request.toModel()));
    }
}
