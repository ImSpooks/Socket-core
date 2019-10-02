package me.ImSpooks.core.packets.init;

import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
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
        /*System.out.println("encoded = " + Global.GSON.toJson(out.getOut()));
        System.out.println("encoded b = " + Global.GSON.toJson(out.getOut()).getBytes());*/
        return Global.GSON.toJson(out.getOut()).getBytes();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet> T deserialize(byte[] input) throws IOException {
        try {
            String decoded = new String(input).trim();
            if (!decoded.startsWith("[")) decoded = "[" + decoded;

            /*System.out.println("decoded = " + decoded);
            System.out.println("decoded b = " + input);*/

            ArrayList<Object> data = Global.GSON.fromJson(decoded, ArrayList.class);
            WrappedInputStream in = new WrappedInputStream(data);
            
            int packetId = in.readInt();
            Packet packet = PacketRegister.createInstance(packetId);

            packet.setType(PacketRegister.getPacketType(packetId));
            packet.receive(in);

            return (T) packet;
        } catch (IOException e) {
            throw new JsonParseException(e);
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

    @Override
    public String toString() {
        String json = Global.GSON.toJson(this);
        json = json.substring(1, json.length() - 1);
        return this.getClass().getSimpleName() + "[" + json + "]";
    }
}
