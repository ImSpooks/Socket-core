package me.ImSpooks.core.server;

import lombok.Getter;
import me.ImSpooks.core.common.json.JSONConfig;
import me.ImSpooks.core.database.IDatabase;
import me.ImSpooks.core.packets.handler.PacketHandler;
import me.ImSpooks.core.server.init.Server;

/**
 * Created by Nick on 27 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class CoreServer {

    @Getter private Server server;

    @Getter private final String password;

    @Getter private PacketHandler packetHandler;

    @Getter private IDatabase database;

    public CoreServer(String password, int port) {
        this.password = password;

        this.packetHandler = new PacketHandler(this, this.password);
        this.server = new Server(port, this);


        JSONConfig config = new JSONConfig("database.json");

        config.expect("type", "MYSQL");
        config.expect("target", "127.0.0.1");
        config.expect("port", 3306);
        config.expect("username", "java");
        config.expect("password", "INSERT HERE");

        if (config.getString("type").equalsIgnoreCase("MYSQL")) {

        }
    }
}
