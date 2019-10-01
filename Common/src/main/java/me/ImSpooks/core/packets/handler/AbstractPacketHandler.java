package me.ImSpooks.core.packets.handler;

import me.ImSpooks.core.common.client.Client;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.PacketRegister;
import org.tinylog.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class AbstractPacketHandler {

    private Map<Class<? extends Packet>, Method> METHODS = new HashMap<>();

    public AbstractPacketHandler() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.getName().startsWith("handle"))
                continue;

            for (Class<? extends Packet> packet : PacketRegister.getPackets()) {
                if (method.getParameterTypes()[0] == Packet.class)
                    continue;

                if (method.getParameterTypes()[0] == packet) {
                    method.setAccessible(true);
                    METHODS.put(packet, method);
                }
            }
        }
    }

    public void handlePacket(Packet packet, Client client) {
        try {
            METHODS.get(packet.getClass()).invoke(this, packet, client);
            return;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            // Method not found
            Logger.error(e, "There was en error thrown while invoking handling method for packet \'{}\'", packet.getClass().getName());
        }
        Logger.warn("There is no packet handler found for packet \'{}\'", packet.getClass().getSimpleName());
    }
}
