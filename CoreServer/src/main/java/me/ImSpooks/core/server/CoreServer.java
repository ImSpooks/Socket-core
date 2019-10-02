package me.ImSpooks.core.server;

import lombok.Getter;
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

    public CoreServer(String password, int port) {
        this.password = password;

        this.packetHandler = new PacketHandler(this.password);
        this.server = new Server(port, this);
    }
}
