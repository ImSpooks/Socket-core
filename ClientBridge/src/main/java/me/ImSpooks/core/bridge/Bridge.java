package me.ImSpooks.core.bridge;

import lombok.Getter;
import me.ImSpooks.core.bridge.init.ClientBridgeServer;
import me.ImSpooks.core.client.CoreClient;

/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */
public class Bridge {

    @Getter private final int bridgePort;
    @Getter private final CoreClient javaClient;
    @Getter private ClientBridgeServer server;

    public Bridge(int bridgePort, CoreClient javaClient) {
        this.bridgePort = bridgePort;
        this.server = new ClientBridgeServer(this.bridgePort, this);

        this.javaClient = javaClient;

        this.javaClient.connect();

        // TODO
    }
}