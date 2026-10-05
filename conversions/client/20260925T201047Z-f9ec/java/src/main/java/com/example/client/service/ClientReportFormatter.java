package com.example.client.service;

import com.example.client.model.ClientSettings;
import com.example.client.model.ClientSettings.ConnectionType;
import com.example.client.model.ClientSettings.DisconnectMode;
import com.example.client.model.ClientSettings.SqlRules;
import com.example.client.model.ClientSettings.SyncpointMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class ClientReportFormatter {

    public List<String> format(ClientSettings settings) {
        List<String> lines = new ArrayList<>();
        lines.add(" ");
        lines.add("SQL CONNECTION TYPE");
        lines.add("===================");
        lines.add(" ");

        if (settings.connectionType() == ConnectionType.SQL_CONNECT_1) {
            lines.add(" Enforces the rules for Remote Unit of Work (RUOW) from previous releases.");
            lines.add("TYPE = SQL-1");
        }
        
        if (settings.connectionType() == ConnectionType.SQL_CONNECT_2) {
            lines.add(" Supports the multiple database pre unit of work semantics DUOW.");
            lines.add("TYPE = SQL-2");
        }

        lines.add(" ");
        lines.add("SQL RULES");
        lines.add("=========");
        lines.add(" ");

        if (settings.rules() == SqlRules.SQL_RULES_DB2) {
            lines.add(" Enables the SQL CONNECT statement to switch the current connection to an established (dormant) connection.");
            lines.add("TYPE = SQL-DB2");
        }
        
        if (settings.rules() == SqlRules.SQL_RULES_STD) {
            lines.add("Permits the establishement of a new connection only through SQL CONNECT statement.");
        }
        lines.add("Under SQL_STD, the SQL SET CONNECTION statement is used to switch the current connection to a dormant connection.");
        lines.add("TYPE = SQL-STD");

        lines.add(" ");
        lines.add("SQL DISCONNECT");
        lines.add("==============");
        lines.add(" ");

        if (settings.disconnect() == DisconnectMode.SQL_DISCONNECT_EXPL) {
            lines.add(" Breaks those connections that have been explicitly marked for release at commit by the SQL RELEASE statement.");
        }
        lines.add("TYPE = SQL-EXPLICIT");
        if (settings.disconnect() == DisconnectMode.SQL_DISCONNECT_COND) {
            lines.add(" Breaks those connections that have no open WITH HOLD cursors at commit, and those that have been marked for release by the SQL RELEASE statement.");
        }
        lines.add("TYPE = SQL-CONDITIONAL");
        if (settings.disconnect() == DisconnectMode.SQL_DISCONNECT_AUTO) {
            lines.add(" Breaks all connections at commit.");
        }
        lines.add("TYPE = SQL-AUTOMATIC");

        lines.add(" ");
        lines.add("SQL SYNCPOINT");
        lines.add("=============");
        lines.add(" ");

        if (settings.syncpoint() == SyncpointMode.SQL_SYNC_TWOPHASE) {
            lines.add(" Requires a Transaction Manager (TM) to coordinate two-phase commits among databases that support this protocol.");
        }
        lines.add("TYPE = SQL-TWOPHASE");
        if (settings.syncpoint() == SyncpointMode.SQL_SYNC_ONEPHASE) {
            lines.add(" Uses one-phase commits to commit the work done by each database in multiple database transactions. Enforces single updater, multiple read behaviour.");
        }
        lines.add("TYPE = SQL-ONEPHASE");
        if (settings.syncpoint() == SyncpointMode.SQL_SYNC_NONE) {
            lines.add(" Does not enforce two-phase commits, or single updater, multiple read behaviour.");
        }
        lines.add("TYPE = SQL-NONE");
        return List.copyOf(lines);
    }
}
