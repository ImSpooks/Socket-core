package me.ImSpooks.core.packets.init;

import me.ImSpooks.core.helpers.JavaHelpers;
import org.tinylog.Logger;

import java.util.*;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class PacketReceiver {

    private final Map<Class<? extends Packet>, List<IncomingPacket<? extends Packet>>> listeners = new HashMap<>();
    private final Set<IncomingPacket<? extends Packet>> globalListeners = new HashSet<>();

    public PacketReceiver() {
        for (Class<? extends Packet> packet : PacketRegister.getPackets()) {
            listeners.put(packet, new LinkedList<>());
        }
    }

    public void received(Packet packet) {
        synchronized (this.listeners) {
            if (this.listeners.get(packet.getClass()).isEmpty())
                return;

            try {
                if (this.listeners.get(packet.getClass()).isEmpty()) {
                    return;
                }

                IncomingPacket<? extends Packet> registered = this.listeners.get(packet.getClass()).get(0);

                if (registered == null) {
                    return;
                }

                // only handles one packet receiver
                if (registered.receive(packet))
                    this.listeners.get(packet.getClass()).remove(0);

                // handle next one if this multiple listeners are using the same packet
                if (registered.handleMultiple())
                    this.received(packet);
            } catch (NullPointerException e) {
                Logger.debug(e);
                JavaHelpers.sleep(1);
            }
        }
    }

    public void removeExpired() {
        synchronized (this.listeners) {
            for (List<IncomingPacket<? extends Packet>> set : this.listeners.values()) {
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
        if (packet == Packet.class) {
            this.globalListeners.add(incomingPacket);
        }
        else {
            this.listeners.get(packet).add(incomingPacket);
        }
    }
}
