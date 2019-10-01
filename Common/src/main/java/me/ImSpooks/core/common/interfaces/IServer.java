package me.ImSpooks.core.common.interfaces;

import me.ImSpooks.core.packets.init.Packet;

/**
 * Created by Nick on 27 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public interface IServer {

    void handleServer();
    void handleClients();

    void write(String server, Packet packet);

    void close();
}
