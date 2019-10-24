package me.ImSpooks.core.packets.collection.network;

import lombok.Getter;
import me.ImSpooks.core.enums.ClientType;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nick on 01 okt. 2019.
 * Copyright © ImSpooks
 */
@Getter
public class PacketRequestConnection extends Packet {

    private String password;
    private long randomKey;
    private String clientName;
    private ClientType clientType;

    public PacketRequestConnection(String password, long randomKey, String clientName, ClientType clientType) {
        this.password = password;
        this.randomKey = randomKey;
        this.clientName = clientName;
        this.clientType = clientType;
    }

    public PacketRequestConnection() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        if (SharedEncryption.getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        String encrypted = SharedEncryption.getEncryption().encryptCollection(this.password, this.randomKey);
        out.writeString(encrypted);
        out.writeString(this.clientName);
        out.writeInt(this.clientType.getId());
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        if (SharedEncryption.getEncryption() == null)
            throw new RuntimeException("Encryption was not yet initialized");

        String encrypted = in.readString();

        ArrayList result = SharedEncryption.getEncryption().decryptCollection(encrypted);
        this.password = (String) result.get(0);
        this.randomKey = (Long) result.get(1);

        this.clientName = in.readString();
        this.clientType = ClientType.getFromId(in.readInt());
    }
}
