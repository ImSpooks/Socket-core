package me.ImSpooks.core.packets.init;

import me.ImSpooks.core.packets.collection.client.PacketClientName;
import me.ImSpooks.core.packets.type.PacketType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class PacketRegister {
    private static final Map<Integer, Class<? extends Packet>> REGISTERED_PACKETS = new HashMap<>();
    private static final Map<Class<? extends Packet>, Integer> REGISTERED_IDS = new HashMap<>();
    private static final Map<Integer, PacketType> PACKET_TYPES = new HashMap<>();

    static {
        register(100, PacketClientName.class, PacketType.NETWORK);


        register(-1, Packet.class, PacketType.OTHER);
    }

    private static void register(int id, Class<? extends Packet> packet, PacketType packetType) {
        if (REGISTERED_PACKETS.containsKey(id)) {
            throw new IllegalArgumentException("Packet with ID " + id + " already registered");
        }
        if (REGISTERED_IDS.containsKey(packet)) {
            throw new IllegalArgumentException("Packet " + packet + " already registered");
        }

        REGISTERED_PACKETS.put(id, packet);
        REGISTERED_IDS.put(packet, id);
        PACKET_TYPES.put(id, packetType);
    }

    public static Packet createInstance(int id) {
        if (id < 0 || id > Byte.MAX_VALUE && !String.valueOf(id).startsWith("-"))
            throw new IllegalArgumentException("Illegal id range " + id);

        try {
            Class<? extends Packet> p = REGISTERED_PACKETS.get(id);
            if (p == null)
                throw new IllegalArgumentException("Unknown packet ID " + id);

            Constructor<? extends Packet> s = p.getDeclaredConstructor();
            s.setAccessible(true);
            return s.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static short getId(Packet packet) {
        if (packet == null)
            throw new IllegalArgumentException("Packet may not be null");

        Integer id = REGISTERED_IDS.get(packet.getClass());
        if (id == null)
            throw new IllegalArgumentException("Unknown packet ID " + packet.getClass().getName());
        if (id < 0 || id > Byte.MAX_VALUE)
            throw new AssertionError("Byte had impossible value " + id);

        return (byte) ((int) id);
    }

    public static PacketType getPacketType(int id) {
        if (id < 0 || id > Byte.MAX_VALUE && !String.valueOf(id).startsWith("-"))
            throw new IllegalArgumentException("Illegal id range " + id);
        if (!PACKET_TYPES.containsKey(id))
            throw new IllegalArgumentException("Invalid packit id " + id);
        return PACKET_TYPES.get(id);
    }

    public static List<Class<? extends Packet>> getPackets() {
        return new ArrayList<>(REGISTERED_IDS.keySet());
    }
}
