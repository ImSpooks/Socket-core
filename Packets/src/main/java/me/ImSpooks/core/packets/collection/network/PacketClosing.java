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
public class PacketClosing extends Packet {

    public PacketClosing() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
    }
}