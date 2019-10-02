package me.ImSpooks.core.packets.collection.network;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;

import java.io.IOException;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class PacketRequestConnection extends Packet {

    @Getter private String password;
    @Getter private long randomKey;
    @Getter private String clientName;

    public PacketRequestConnection(String password, long randomKey, String clientName) {
        this.password = password;
        this.randomKey = randomKey;
        this.clientName = clientName;
    }

    public PacketRequestConnection() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        if (SharedEncryption.getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        byte[] encrypted = SharedEncryption.getEncryption().encrypt(this.password, this.randomKey);
        out.write(encrypted);
        out.writeString(this.clientName);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        if (SharedEncryption.getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        byte[] encrypted = in.readBytes();

        Object[] result = SharedEncryption.getEncryption().decrypt(encrypted, String.class, Long.class);
        this.password = (String) result[0];
        this.randomKey = (long) result[1];

        this.clientName = in.readString();
    }
}
