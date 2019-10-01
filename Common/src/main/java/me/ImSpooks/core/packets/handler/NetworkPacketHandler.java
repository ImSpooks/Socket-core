package me.ImSpooks.core.packets.handler;

import me.ImSpooks.core.common.client.Client;
import me.ImSpooks.core.packets.collection.client.PacketClientName;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class NetworkPacketHandler extends AbstractPacketHandler {

    private static NetworkPacketHandler instance;
    public static NetworkPacketHandler getInstance() {
        if (instance == null)
            instance = new NetworkPacketHandler();
        return instance;
    }

    public NetworkPacketHandler() {
        super();
    }

    private void handlePacket(PacketClientName packet, Client client) {
        client.setClientName(packet.getClientName());
        System.out.println("Clients new name is " + client.getClientName());
    }
}
