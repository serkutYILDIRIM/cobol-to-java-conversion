package com.example.client.dto;

import com.example.client.model.ClientSettings;
import com.example.client.model.ClientSettings.ConnectionType;
import com.example.client.model.ClientSettings.DisconnectMode;
import com.example.client.model.ClientSettings.SqlRules;
import com.example.client.model.ClientSettings.SyncpointMode;

public record ClientSettingsDto(
        ConnectionType connectionType,
        SqlRules rules,
        DisconnectMode disconnect,
        SyncpointMode syncpoint) {

    public static ClientSettingsDto from(ClientSettings settings) {
        return new ClientSettingsDto(
                settings.connectionType(),
                settings.rules(),
                settings.disconnect(),
                settings.syncpoint());
    }
}
