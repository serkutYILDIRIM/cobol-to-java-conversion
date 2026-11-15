package com.example.inventory.model;

import java.util.List;

public final class EmbeddedInventory {
    private static final List<String> PRODUCTS = List.of(
            "P1001WIRELESS MOUSE      00120000500002490A",
            "P1002MECHANICAL KEYBOARD 00045000300008950A",
            "P100327 INCH MONITOR     00018000200024999A",
            "P1004USB-C HUB           00200000750003400A",
            "P1005LAPTOP STAND        00060000250004275A",
            "P1006NOISE CANCEL HEADSET00012000150015900A",
            "P1007HD WEBCAM           00035000200005990A",
            "P1008EXTERNAL SSD 1TB    00025000100011900A",
            "P1009DOCKING STATION     00008000100018900I",
            "P1010HDMI CABLE 2M       00300001000000750A");

    private static final List<String> TRANSACTIONS = List.of(
            "P1001S+00080", "P1002R+00040", "P1003S+00025",
            "P1004S+00150", "P1005A-00005", "P9999R+00010",
            "P1006R+00030", "P1007X+00005", "P1009S+00002",
            "P1008S+00018", "P1010S+00220", "P1007S+00020");

    private EmbeddedInventory() {
    }
}
