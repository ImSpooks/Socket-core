package me.ImSpooks.core.database;

import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.database.data.DataKey;
import me.ImSpooks.core.database.data.DataValue;

import java.util.Map;

/**
 * Created by Nick on 03 okt. 2019.
 * Copyright © ImSpooks
 */
public class ISubDatabase<T> {

    @Getter @Setter private IDatabase parent;
    @Getter private final String table;
    @Getter private DataKey<T> parentKey;


    public ISubDatabase(String table, DataKey<T> parentKey) {
        this.table = table;
        this.parentKey = parentKey;
    }

    public void update(T key, String column, Object data) {
        parent.update(this.table, parentKey.withValue(key), column, data);
    }

    public DataValue getData(T key, String column) {
        return parent.getData(table, parentKey.withValue(key), column);
    }

    public Map<String, DataValue> getRow(T key) {
        return parent.getRow(table, parentKey.withValue(key));
    }

    public Map<String, DataValue> getRow(int row) {
        return parent.getRow(table, row);
    }
}
