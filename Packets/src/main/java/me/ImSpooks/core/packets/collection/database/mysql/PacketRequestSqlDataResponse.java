package me.ImSpooks.core.packets.collection.database.mysql;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.channels.WrappedInputStream;
import me.ImSpooks.core.packets.init.channels.WrappedOutputStream;

import java.io.IOException;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class PacketRequestSqlDataResponse extends Packet {

    @Getter private Object data = "test?";

    public PacketRequestSqlDataResponse(Object data) {

    }

    public PacketRequestSqlDataResponse() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeTypePrefixed(data);

    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.data = in.readTypePrefixed();
    }
}
