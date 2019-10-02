package me.ImSpooks.core.packets.collection.network;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;

import java.io.IOException;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class PacketConfirmConnection extends Packet {

    @Getter private long randomKey;

    protected PacketConfirmConnection() {}

    public PacketConfirmConnection(long randomKey) {
        this.randomKey = randomKey;
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        if (SharedEncryption.getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        byte[] encrypted = SharedEncryption.getEncryption().encrypt(this.randomKey);
        out.write(encrypted);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        if (SharedEncryption.getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        byte[] encrypted = in.readBytes();

        Object[] result = SharedEncryption.getEncryption().decrypt(encrypted, Long.class);
        this.randomKey = (long) result[0];
    }
}
