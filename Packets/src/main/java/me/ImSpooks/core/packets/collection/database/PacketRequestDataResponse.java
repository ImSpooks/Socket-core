package me.ImSpooks.core.packets.collection.database;

import lombok.Getter;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.collection.GlobalPacket;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import org.bson.Document;

import java.io.IOException;

/**
 * Created by Nick on 09 okt. 2019.
 * Copyright © ImSpooks
 */
@GlobalPacket
public class PacketRequestDataResponse extends Packet {

    @Getter private Document document;

    public PacketRequestDataResponse(Document document) {
        this.document = document;
    }

    public PacketRequestDataResponse() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeString(Global.GSON.toJson(this.document));
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.document = Global.GSON.fromJson(in.readString(), Document.class);
    }
}
