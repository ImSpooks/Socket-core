package me.ImSpooks.core.packets.handler.handlers;

import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.collection.database.mysql.PacketRequestSqlData;
import me.ImSpooks.core.packets.collection.database.mysql.PacketRequestSqlDataResponse;
import me.ImSpooks.core.packets.handler.PacketHandler;
import me.ImSpooks.core.packets.handler.SubPacketHandler;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class DatabasePacketHandler extends SubPacketHandler {

    public DatabasePacketHandler(PacketHandler packetHandler) {
        super(packetHandler);
    }

    private void handlePacket(PacketRequestSqlData packet, AbstractClient client) {
        client.write(new PacketRequestSqlDataResponse("test string"));
    }
}
