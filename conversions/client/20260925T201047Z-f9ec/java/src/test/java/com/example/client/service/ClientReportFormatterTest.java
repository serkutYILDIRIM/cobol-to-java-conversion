package com.example.client.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.client.model.ClientSettings;
import com.example.client.model.ClientSettings.ConnectionType;
import com.example.client.model.ClientSettings.DisconnectMode;
import com.example.client.model.ClientSettings.SqlRules;
import com.example.client.model.ClientSettings.SyncpointMode;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClientReportFormatterTest {

    private final ClientReportFormatter formatter = new ClientReportFormatter();

    @Test
    void preservesSourceSentenceBoundariesAndDisplaySpacing() {
        ClientSettings settings = new ClientSettings(ConnectionType.SQL_CONNECT_1,
                SqlRules.SQL_RULES_DB2, DisconnectMode.SQL_DISCONNECT_EXPL, SyncpointMode.SQL_SYNC_ONEPHASE);

        assertThat(formatter.format(settings)).isEqualTo(List.of(
                " ",
                "SQL CONNECTION TYPE",
                "===================",
                " ",
                " Enforces the rules for Remote Unit of Work (RUOW) from previous releases.",
                "TYPE = SQL-1",
                " ",
                "SQL RULES",
                "=========",
                " ",
                " Enables the SQL CONNECT statement to switch the current connection to an established (dormant) connection.",
                "TYPE = SQL-DB2",
                "Under SQL_STD, the SQL SET CONNECTION statement is used to switch the current connection to a dormant connection.",
                "TYPE = SQL-STD",
                " ",
                "SQL DISCONNECT",
                "==============",
                " ",
                " Breaks those connections that have been explicitly marked for release at commit by the SQL RELEASE statement.",
                "TYPE = SQL-EXPLICIT",
                "TYPE = SQL-CONDITIONAL",
                "TYPE = SQL-AUTOMATIC",
                " ",
                "SQL SYNCPOINT",
                "=============",
                " ",
                "TYPE = SQL-TWOPHASE",
                " Uses one-phase commits to commit the work done by each database in multiple database transactions. Enforces single updater, multiple read behaviour.",
                "TYPE = SQL-ONEPHASE",
                "TYPE = SQL-NONE"));
    }
}
