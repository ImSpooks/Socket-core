package me.ImSpooks.core.packets.type;

import lombok.RequiredArgsConstructor;

/**
 * Created by Nick on 01 okt. 2019.
 * Copyright © ImSpooks
 */
@RequiredArgsConstructor
public enum PacketType {

    NETWORK(100),
    DATABASE(200),
    OTHER(300);

    public final int START_ID;
}
