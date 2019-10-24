package me.ImSpooks.core.common.interfaces;

import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.init.Packet;

/**
 * Created by Nick on 27 sep. 2019.
 * Copyright © ImSpooks
 */
public interface IServer {

    void handleServer(AbstractClient client);
    void handleClients();

    void write(String server, Packet packet);

    void close();
}
