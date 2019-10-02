package me.ImSpooks.core.packets.init;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public abstract class IncomingPacket<T extends Packet> {

    private final long registerTime = System.currentTimeMillis();
    private final long expireAfter;

    public IncomingPacket(long expireAfter) {
        this.expireAfter = expireAfter;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - this.registerTime >= this.expireAfter;
    }

    @SuppressWarnings("unchecked")
    public boolean receive(Packet packet) {
        return this.onReceive((T) packet);
    }

    public abstract boolean onReceive(T packet);
    public abstract void onExpire();
}
