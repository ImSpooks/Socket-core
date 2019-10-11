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
            if (this.listeners.get(packet.getClass()).isEmpty())
                return;

            IncomingPacket<? extends Packet> registered = this.listeners.get(packet.getClass()).iterator().next();

            if (registered == null) {
                return;
            }

            // only handles one packet receiver
            if (registered.receive(packet)) {
                this.listeners.get(packet.getClass()).remove(registered);

                // handle next one if this multiple listeners are using a single packet
            }
            if (registered.handleMultiple())
                this.received(packet);
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
