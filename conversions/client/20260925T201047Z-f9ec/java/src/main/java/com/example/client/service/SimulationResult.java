package com.example.client.service;

import com.example.client.model.ClientSettings;
import java.util.List;

public record SimulationResult(
        ClientSettings initial,
        ClientSettings changed,
        ClientSettings restored,
        List<String> outputLines) {
}
