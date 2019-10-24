package me.ImSpooks.core.packets.collection.other;

import lombok.Getter;
import me.ImSpooks.core.packets.collection.GlobalPacket;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.IOException;

/**
 * Created by Nick on 17 okt. 2019.
 * Copyright © ImSpooks
 */
@GlobalPacket
public class PacketPing extends Packet {

    @Getter private long clientTime;

    public PacketPing(long clientTime) {
        this.clientTime = clientTime;
    }

    public PacketPing() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeLong(this.clientTime);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.clientTime = in.readLong();
    }
}
