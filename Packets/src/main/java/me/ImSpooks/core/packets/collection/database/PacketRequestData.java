package me.ImSpooks.core.packets.collection.database;

import lombok.Getter;
import me.ImSpooks.core.packets.collection.GlobalPacket;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.IOException;

/**
 * Created by Nick on 09 okt. 2019.
 * Copyright © ImSpooks
 */
@Getter
@GlobalPacket
public class PacketRequestData extends Packet {

    private String collection;
    private String key;
    private Object value;

    public PacketRequestData(String collection, String key, Object value) {
        this.collection = collection;
        this.key = key;
        this.value = value;
    }

    public PacketRequestData() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeString(this.collection);
        out.writeString(this.key);
        out.writeTypePrefixed(this.value);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.collection = in.readString();
        this.key = in.readString();
        this.value = in.readTypePrefixed();
    }
}
