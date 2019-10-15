package me.ImSpooks.core.packets.handler;

import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.PacketRegister;
import org.tinylog.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 01 okt. 2019.
 * Copyright © ImSpooks
 */
public class SubPacketHandler {

    protected PacketHandler packetHandler;

    private Map<Class<? extends Packet>, Method> METHODS = new HashMap<>();

    public SubPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;

        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.getName().startsWith("handle"))
                continue;

            for (Class<? extends Packet> packet : PacketRegister.getPackets()) {
                if (method.isAnnotationPresent(PacketHandling.class)) {
                    if (method.getParameterTypes()[0] == Packet.class)
                        continue;

                    if (method.getParameterTypes()[0] == packet) {
                        method.setAccessible(true);
                        METHODS.put(packet, method);
                    }
                }
            }
        }
    }

    public boolean handlePacket(Packet packet, AbstractClient client) {
        if (!METHODS.containsKey(packet.getClass())) {
            Logger.warn("There is no packet handler found for packet \'{}\'", packet.getClass().getSimpleName());
            return false;
        }

        try {
            METHODS.get(packet.getClass()).invoke(this, packet, client);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            // Method not found
            Logger.error(e, "There was en error thrown while invoking handling method for packet \'{}\'", packet.getClass().getName());
            return false;
        }
    }
}
