package com.example.client.model;

import java.util.Objects;

public record ClientSettings(
        ConnectionType connectionType,
        SqlRules rules,
        DisconnectMode disconnect,
        SyncpointMode syncpoint) {

    public ClientSettings {
        Objects.requireNonNull(connectionType, "connectionType");
        Objects.requireNonNull(rules, "rules");
        Objects.requireNonNull(disconnect, "disconnect");
        Objects.requireNonNull(syncpoint, "syncpoint");
    }

    public enum ConnectionType { SQL_CONNECT_1, SQL_CONNECT_2 }
    public enum SqlRules { SQL_RULES_DB2, SQL_RULES_STD }
    public enum DisconnectMode { SQL_DISCONNECT_EXPL, SQL_DISCONNECT_COND, SQL_DISCONNECT_AUTO }
    public enum SyncpointMode { SQL_SYNC_TWOPHASE, SQL_SYNC_ONEPHASE, SQL_SYNC_NONE }
}
