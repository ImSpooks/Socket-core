package me.ImSpooks.core.client;

import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.client.init.Client;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.init.IncomingPacket;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.PacketReceiver;

/**
 * Created by Nick on 26 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class CoreClient {

    @Getter private final long verificationId = Global.RANDOM.nextLong();

    @Getter private PacketReceiver packetReceiver;
    @Getter private Client client;

    @Getter private final String ip;
    @Getter private final int port;
    @Getter private final String password;
    @Getter private final String clientName;

    @Getter @Setter private Runnable coreConnected = () -> {};

    public CoreClient(String ip, int port, String password, String clientName) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.clientName = clientName;
        this.packetReceiver = new PacketReceiver();

        this.client = new Client(this.ip, this.port, this.clientName, this);

    }

    public void connect() {
        this.client.connect();
    }

    public void sendPacket(Packet packet) {
        this.client.write(packet);
    }

    public void sendAndReadPacket(Packet packet, Class<? extends Packet> responseClass, IncomingPacket<? extends Packet> response) {
        this.packetReceiver.addListener(responseClass, response);
        this.client.write(packet);
    }
}
