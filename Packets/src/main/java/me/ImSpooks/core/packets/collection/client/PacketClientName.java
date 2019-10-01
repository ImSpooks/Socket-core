package me.ImSpooks.core.packets.collection.client;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.WrappedInputStream;
import me.ImSpooks.core.packets.init.WrappedOutputStream;

import java.io.IOException;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class PacketClientName extends Packet {

    @Getter private String clientName;

    public PacketClientName(String clientName) {
        this.clientName = clientName;
    }

    public PacketClientName() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeString(this.clientName);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.clientName = in.readString();
    }
}
