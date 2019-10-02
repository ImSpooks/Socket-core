package me.ImSpooks.core.packets.init;

import java.util.*;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class PacketReceiver {

    private final Map<Class<? extends Packet>, Set<IncomingPacket<? extends Packet>>> listeners = new HashMap<>();

    public PacketReceiver() {
        for (Class<? extends Packet> packet : PacketRegister.getPackets()) {
            listeners.put(packet, new HashSet<>());
        }
    }

    public void received(Packet packet) {
        synchronized (this.listeners) {
            Set<IncomingPacket<? extends Packet>> registered = this.listeners.get(packet.getClass());

            if (registered == null || registered.isEmpty()) {
                return;
            }
            registered.removeIf(listener -> listener.receive(packet));
        }
    }

    public void removeExpired() {
        synchronized (this.listeners) {
            for (Set<IncomingPacket<? extends Packet>> set : this.listeners.values()) {
                Iterator<IncomingPacket<? extends Packet>> it = set.iterator();

                while (it.hasNext()) {
                    IncomingPacket<? extends Packet> next = it.next();
                    if (next.hasExpired()) {
                        it.remove();
                        next.onExpire();
                    }
                }
            }
        }
    }

    public void addListener(Class<? extends Packet> packet, IncomingPacket<? extends Packet> incomingPacket) {
        this.listeners.get(packet).add(incomingPacket);
    }
}
