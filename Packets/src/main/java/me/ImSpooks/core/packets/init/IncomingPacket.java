package me.ImSpooks.core.packets.init;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public abstract class IncomingPacket<T extends Packet> {

    private final long registerTime = System.currentTimeMillis();
    private final long expireAfter;
    private final boolean handleMultiple;

    public IncomingPacket(long expireAfter) {
        this.expireAfter = expireAfter;
        this.handleMultiple = false;
    }

    public IncomingPacket(long expireAfter, boolean handleMultiple) {
        this.expireAfter = expireAfter;
        this.handleMultiple = true;
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

    public boolean handleMultiple() {
        return this.handleMultiple;
    }
}
