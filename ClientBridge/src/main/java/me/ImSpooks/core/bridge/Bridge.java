package me.ImSpooks.core.bridge;

import lombok.Getter;
import me.ImSpooks.core.bridge.init.ClientBridgeServer;
import me.ImSpooks.core.client.CoreClient;

/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */
public class Bridge {

    @Getter private final CoreClient javaClient;
    @Getter private ClientBridgeServer server;

    public Bridge(CoreClient javaClient) {
        this.javaClient = javaClient;

        this.javaClient.connect();

        // TODO
    }
}