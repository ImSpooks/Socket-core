package me.ImSpooks.core.packets.init;

import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.collection.network.PacketResponseExpired;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import me.ImSpooks.core.packets.type.PacketType;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nick on 27 sep. 2019.
 * Copyright © ImSpooks
 */
public abstract class Packet {

    @Getter private @Setter PacketType type;

    public String serialize() {
        WrappedOutputStream out = new WrappedOutputStream();
        try {
            out.writeInt(this.getId());
            this.send(out);
        } catch (IOException e) {
            Logger.error(e, "Something went wrong while serializing packet {}", this.getClass().getSimpleName());
        }
        return Global.GSON.toJson(out.getOut());
    }

    public byte[] serializeBytes() {
        return this.serialize().getBytes();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet> T deserialize(String input) throws Exception {
        int packetId = -1;
        try {
            input = input.trim().replace("\r", "");
            if (!input.startsWith("[")) input = "[" + input;

            ArrayList<Object> data = Global.GSON.fromJson(input, ArrayList.class);
            WrappedInputStream in = new WrappedInputStream(data);

            packetId = in.readInt();
            Packet packet = PacketRegister.createInstance(packetId);

            packet.setType(PacketRegister.getPacketType(packetId));
            packet.receive(in);

            return (T) packet;
        } catch (IOException e) {
            return (T) new PacketResponseExpired(-1);
        } catch (JsonSyntaxException e) {
            String packetName = "unknown";
            try {
                packetName = PacketRegister.getPacketName(packetId);
            } catch (Exception ignored) {}

            Logger.error(e, "Deserializing json went wrong for packet {} with input \'{}\'", packetId != -1 ? packetName : "\"unknown\"", input);
            return (T) new PacketResponseExpired(-1);
        } catch (Exception e) {
            String packetName = "unknown";
            try {
                packetName = PacketRegister.getPacketName(packetId);
            } catch (Exception ignored) {}
            Logger.error(e, "Something went wrong while deserializing packet {} with input \'{}\'", packetId != -1 ? packetName : "\"unknown\"", input);
            return (T) new PacketResponseExpired(-1);
        }
    }

    public static <T extends Packet> T deserialize(byte[] input) throws Exception {
        return deserialize(new String(input));
    }

    public abstract void send(WrappedOutputStream out) throws IOException;
    public abstract void receive(WrappedInputStream in) throws IOException;

    private int id = -1;
    public int getId() {
        if (this.id == -1)
            this.id = PacketRegister.getId(this);
        return this.id;
    }

    @Override
    public String toString() {
        this.getId();
        String json = Global.GSON.toJson(this);
        json = json.substring(1, json.length() - 1);
        return this.getClass().getSimpleName() + "[" + json + "]";
    }
}
