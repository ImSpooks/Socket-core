package me.ImSpooks.core.packets.init;

import me.ImSpooks.core.packets.collection.database.*;
import me.ImSpooks.core.packets.collection.network.PacketConfirmConnection;
import me.ImSpooks.core.packets.collection.network.PacketRequestConnection;
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

    private final static int MAX_PACKETS = (Short.MAX_VALUE + 1) * 2;

    static {
        // networking
        register(1, PacketRequestConnection.class, PacketType.NETWORK);
        register(2, PacketConfirmConnection.class, PacketType.NETWORK);

        // database=
        register(1, PacketRequestData.class, PacketType.DATABASE);
        register(2, PacketRequestDataResponse.class, PacketType.DATABASE);
        register(3, PacketRequestCollection.class, PacketType.DATABASE);
        register(4, PacketRequestCollectionResponse.class, PacketType.DATABASE);
        register(5, PacketUpdateData.class, PacketType.DATABASE);

        //other
    }

    private static void register(int id, Class<? extends Packet> packet, PacketType packetType) {
        id = packetType.START_ID + id - 1;

        if (REGISTERED_PACKETS.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Packet with ID %s already registered for type %s", id - packetType.START_ID + 1, packetType));
        }
        if (REGISTERED_IDS.containsKey(packet)) {
            throw new IllegalArgumentException("Packet " + packet + " already registered");
        }

        REGISTERED_PACKETS.put(id, packet);
        REGISTERED_IDS.put(packet, id);
        PACKET_TYPES.put(id, packetType);
    }

    public static Packet createInstance(int id) {
        if (id < 0 || id > MAX_PACKETS && !String.valueOf(id).startsWith("-"))
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
        if (id < 0 || id > MAX_PACKETS)
            throw new AssertionError("Packet id had impossible value " + id);

        return (short) ((int) id);
    }

    public static PacketType getPacketType(int id) {
        if (id < 0 || id > MAX_PACKETS && !String.valueOf(id).startsWith("-"))
            throw new IllegalArgumentException("Illegal id range " + id);
        if (!PACKET_TYPES.containsKey(id))
            throw new IllegalArgumentException("Invalid packit id " + id);
        return PACKET_TYPES.get(id);
    }

    public static String getPacketName(int id) {
        if (id < 0 || id > MAX_PACKETS && !String.valueOf(id).startsWith("-"))
            throw new IllegalArgumentException("Illegal id range " + id);
        if (!REGISTERED_PACKETS.containsKey(id))
            throw new IllegalArgumentException("Invalid packit id " + id);

        return REGISTERED_PACKETS.get(id).getSimpleName();
    }

    public static List<Class<? extends Packet>> getPackets() {
        return new ArrayList<>(REGISTERED_IDS.keySet());
    }
}
