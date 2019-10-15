package me.ImSpooks.core.common.interfaces;

import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.packets.init.Packet;

/**
 * Created by Nick on 27 sep. 2019.
 * Copyright © ImSpooks
 */
public interface IClient {

    void handlePacket(Packet receivedPacket);
    void handleConnection() throws SocketDisconnectedException;

    void handleClient();

    void connect();

    void write(Packet packet);

    void close();
}
