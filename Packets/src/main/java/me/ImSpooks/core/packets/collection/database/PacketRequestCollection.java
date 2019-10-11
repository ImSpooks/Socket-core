package me.ImSpooks.core.packets.collection.database;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.IOException;

/**
 * Created by Nick on 09 okt. 2019.
 * Copyright © ImSpooks
 */
public class PacketRequestCollection extends Packet {

    @Getter private String collection;

    public PacketRequestCollection(String collection) {
        this.collection = collection;
    }

    public PacketRequestCollection() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeString(this.collection);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.collection = in.readString();
    }
}
