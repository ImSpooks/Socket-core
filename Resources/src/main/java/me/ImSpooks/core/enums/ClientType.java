package me.ImSpooks.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Nick on 21 okt. 2019.
 * Copyright © ImSpooks
 */
@RequiredArgsConstructor
public enum ClientType {
    NULL(-1, ""),
    SERVER(-1, "SERVER"),
    JAVA(0, "JAVA"),
    PHP(1, "PHP"),
    ;

    @Getter private final int id;
    @Getter private final String name;

    public static ClientType[] CACHE = values();

    public static ClientType getFromName(String name) {
        for (ClientType clientType : CACHE) {
            if (clientType.getName().equalsIgnoreCase(name)) {
                return clientType;
            }
        }
        return NULL;
    }

    public static ClientType getFromId(int id) {
        for (ClientType clientType : CACHE) {
            if (clientType.getId() == id) {
                return clientType;
            }
        }
        return NULL;
    }
}
