package me.ImSpooks.core.server;

import lombok.Getter;
import me.ImSpooks.core.common.client.Client;
import me.ImSpooks.core.packets.init.Packet;

/**
 * Created by Nick on 27 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class ServerClient extends Client {

    @Getter private final Server server;

    public ServerClient(String ip, int port, Server server) {
        super(ip, port, "ServerClient");
        this.server = server;
    }

    @Override
    public void handlePacket(Packet receivedPacket) {
        /*if (receivedPacket instanceof PacketTo) {
            PacketTo packet = (PacketTo) receivedPacket;
            this.server.write(packet.getTo(), packet.getPacket());
        }
        else */super.handlePacket(receivedPacket);
    }
}
