package me.ImSpooks.core.packets.init;

import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.type.PacketType;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nick on 27 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public abstract class Packet {

    @Getter private @Setter PacketType type;

    public byte[] serialize(WrappedOutputStream out) {
        try {
            out.write((int) this.getId());
            this.send(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Global.GSON.toJson(out.getOut()).getBytes();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet> T deserialize(byte[] input) {
        try {
            WrappedInputStream in = new WrappedInputStream(Global.GSON.fromJson(new String(input), ArrayList.class));
            
            int packetId = in.readInt();
            Packet packet = PacketRegister.createInstance(packetId);

            packet.setType(PacketRegister.getPacketType(packetId));
            packet.receive(in);

            return (T) packet;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logger.error(e, "Something went wrong while deserializing packet");
        }
        return null;
    }

    public abstract void send(WrappedOutputStream out) throws IOException;
    public abstract void receive(WrappedInputStream in) throws IOException;

    public short getId() {
        return PacketRegister.getId(this);
    }
}
