package me.ImSpooks.core.packets.collection.database;

import lombok.Getter;
import me.ImSpooks.core.helpers.Global;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 09 okt. 2019.
 * Copyright © ImSpooks
 */
@SuppressWarnings("unchecked")
public class PacketRequestCollectionResponse extends Packet {

    @Getter private ArrayList<Document> documents;

    public PacketRequestCollectionResponse(List<Document> documents) {
        if (!(documents instanceof ArrayList))
            throw new IllegalArgumentException("Entered list must be an Array list");

        this.documents = (ArrayList) documents;
    }


    public PacketRequestCollectionResponse() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeString(Global.GSON.toJson(this.documents));
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.documents = Global.GSON.fromJson(in.readString(), ArrayList.class);
    }
}
