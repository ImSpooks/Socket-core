package me.ImSpooks.core.database.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 03 okt. 2019.
 * Copyright © ImSpooks
 */
public class CachedData {

    private List<Map<String, DataValue>> map;

    public CachedData(LinkedHashMap<String, DataValue> map) {
        this.map = new ArrayList<>();
        this.map.add(map);
    }

    public CachedData(List<Map<String, DataValue>> list) {
        this.map = list;
    }

    public void update(DataKey dataKey, String column, DataValue data) {
        this.getRow(dataKey).put(column, data);
    }

    public DataValue getColumn(DataKey dataKey, String column) {
        return this.getRow(dataKey).get(column);
    }

    public Map<String, DataValue> getRow(DataKey dataKey) {
        for (Map<String, DataValue> stringObjectMap : this.map) {
            if (stringObjectMap.get(dataKey.getKey()).getValue() == dataKey.getValue())
                return stringObjectMap;
        }
        return null;
    }

    public boolean containsColumn(String column) {
        for (Map<String, DataValue> stringObjectMap : this.map) {
            if (stringObjectMap.containsKey(column))
                return true;
        }
        return false;
    }

    public List<Map<String, DataValue>> get() {
        return this.map;
    }

    public int addNew(DataKey dataKey) {
        Map<String, DataValue> value = new LinkedHashMap<>();
        value.put(dataKey.getKey(), new DataValue(dataKey.getValue()));
        this.map.add(value);
        return this.map.size();
    }
}
