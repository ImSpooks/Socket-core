package me.ImSpooks.core.packets.init;

import me.ImSpooks.core.helpers.Compressor;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public abstract class CompressedPacket extends Packet {

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        this.write(out);
        byte[] compressed = Compressor.compress(Global.GSON.toJson(out.getOut()).getBytes());

        out = new WrappedOutputStream();
        out.write(compressed);

        System.out.println("out.getOut() = " + out.getOut());
        System.out.println("compressed = " + compressed);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void receive(WrappedInputStream in) throws IOException {
        byte[] bytes = in.readBytes();
        System.out.println("Uncrompressing 1 = " + bytes);
        byte[] uncompressed = Compressor.decompress(bytes);
        System.out.println("Uncrompressing 2");
        in = new WrappedInputStream(Global.GSON.fromJson(new String(uncompressed), ArrayList.class));
        System.out.println("Uncrompressing 3");
        this.read(in);
    }

    public abstract void write(WrappedOutputStream out) throws IOException;
    public abstract void read(WrappedInputStream in) throws IOException;
}
