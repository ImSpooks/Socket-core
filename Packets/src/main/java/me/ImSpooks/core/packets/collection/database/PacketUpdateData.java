package me.ImSpooks.core.packets.collection.database;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.IOException;

/**
 * Created by Nick on 09 okt. 2019.
 * Copyright © ImSpooks
 */
@Getter
public class PacketUpdateData extends Packet {

    private String collection;
    private String key;
    private Object keyValue;
    private String column;
    private Object value;

    public PacketUpdateData(String collection, String key, Object keyValue, String column, Object value) {
        this.collection = collection;
        this.key = key;
        this.keyValue = keyValue;
        this.column = column;
        this.value = value;
    }

    public PacketUpdateData() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeString(this.collection);

        out.writeString(this.key);
        out.writeTypePrefixed(this.keyValue);

        out.writeString(this.column);
        out.writeTypePrefixed(this.value);
    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.collection = in.readString();

        this.key = in.readString();
        this.keyValue = in.readTypePrefixed();

        this.column = in.readString();
        this.value = in.readTypePrefixed();
    }
}
