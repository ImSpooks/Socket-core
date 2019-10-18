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
public class PacketPingResponse extends Packet {

    @Getter private long serverTime;
    @Getter private long estimatedTime;

    public PacketPingResponse(long serverTime, long clientTime) {
        this.serverTime = serverTime;
        this.estimatedTime = serverTime - clientTime + 1;
    }

    public PacketPingResponse() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeLong(this.serverTime);
        out.writeLong(this.estimatedTime);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.serverTime = in.readLong();
        this.estimatedTime = in.readLong();
    }
}
