package me.ImSpooks.core.packets.collection.network;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;

import java.io.IOException;
import java.util.ArrayList;

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

        String encrypted = SharedEncryption.getEncryption().encryptCollection(this.randomKey);
        out.writeString(encrypted);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        if (SharedEncryption.getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        String encrypted = in.readString();

        ArrayList result = SharedEncryption.getEncryption().decryptCollection(encrypted);
        this.randomKey = (Long) result.get(0);
    }
}
