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
public class PacketRequestSqlData extends Packet {

    @Getter private String select;
    @Getter private String from;
    @Getter private String where;
    @Getter private String inner;

    public PacketRequestSqlData(String select, String from, String where, String inner) {
        this.select = select;
        this.from = from;
        this.where = " WHERE " + where;
        this.inner = " " + inner;
    }

    public PacketRequestSqlData(String select, String from, String where) {
        this.select = select;
        this.from = from;
        this.where = " WHERE " + where;
        this.inner = "";
    }

    public PacketRequestSqlData(String select, String from) {
        this.select = select;
        this.from = from;
        this.where = "";
        this.inner = "";
    }

    public PacketRequestSqlData(String from) {
        this.select = "*";
        this.from = from;
        this.where = "";
        this.inner = "";
    }

    public PacketRequestSqlData() {
    }

    @Override
    public void send(WrappedOutputStream out) throws IOException {
        out.writeString(this.select);
        out.writeString(this.from);
        out.writeString(this.where);
        out.writeString(this.inner);

    }

    @Override
    public void receive(WrappedInputStream in) throws IOException {
        this.select = in.readString();
        this.from = in.readString();
        this.where = in.readString();
        this.inner = in.readString();
    }
}
