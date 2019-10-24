package me.ImSpooks.core.packets.collection.network;

import me.ImSpooks.core.packets.collection.GlobalPacket;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.IOException;

/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */
@GlobalPacket
public class PacketResponseExpired extends Packet {

    private long ms;

    public PacketResponseExpired(long ms) {
        this.ms = ms;
    }

    public PacketResponseExpired() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeLong(this.ms);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.ms = in.readLong();
    }
}