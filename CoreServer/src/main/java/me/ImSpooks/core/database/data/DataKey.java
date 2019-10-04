package me.ImSpooks.core.database.data;

import lombok.Getter;

/**
 * Created by Nick on 04 okt. 2019.
 * Copyright © ImSpooks
 */
public class DataKey<T> {

    @Getter private String key;
    @Getter private Object value;

    public DataKey(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public DataKey(String key) {
        this.key = key;
    }

    public DataKey withValue(T value) {
        return new DataKey<T>(this.getKey(), value);
    }
}
