package com.example.client.service;

import com.example.client.model.ClientSettings;
import com.example.client.model.ClientSettings.ConnectionType;
import com.example.client.model.ClientSettings.DisconnectMode;
import com.example.client.model.ClientSettings.SqlRules;
import com.example.client.model.ClientSettings.SyncpointMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public final class ClientSimulationService {

    private static final ClientSettings REQUESTED = new ClientSettings(
            ConnectionType.SQL_CONNECT_2,
            SqlRules.SQL_RULES_STD,
            DisconnectMode.SQL_DISCONNECT_COND,
            SyncpointMode.SQL_SYNC_TWOPHASE);

    private final ClientReportFormatter formatter;

    public ClientSimulationService(ClientReportFormatter formatter) {
        this.formatter = formatter;
    }


}
