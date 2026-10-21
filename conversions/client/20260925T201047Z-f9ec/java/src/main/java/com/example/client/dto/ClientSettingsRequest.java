package com.example.client.dto;

import com.example.client.model.ClientSettings;
import com.example.client.model.ClientSettings.ConnectionType;
import com.example.client.model.ClientSettings.DisconnectMode;
import com.example.client.model.ClientSettings.SqlRules;
import com.example.client.model.ClientSettings.SyncpointMode;
import jakarta.validation.constraints.NotNull;



    public ClientSettings toModel() {
        return new ClientSettings(connectionType, rules, disconnect, syncpoint);
    }
}
