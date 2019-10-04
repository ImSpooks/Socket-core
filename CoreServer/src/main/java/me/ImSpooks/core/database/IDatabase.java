package me.ImSpooks.core.database;

import lombok.Getter;
import me.ImSpooks.core.database.data.CachedData;
import me.ImSpooks.core.database.data.DataKey;
import me.ImSpooks.core.database.data.DataValue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 03 okt. 2019.
 * Copyright © ImSpooks
 */
public abstract class IDatabase {

    @Getter private String target;
    @Getter private String username;
    @Getter private String password;
    @Getter private String database;

    public IDatabase(String target, String username, String password, String database) {
        this.target = target;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    protected Map<String, CachedData> cachedData = new LinkedHashMap<>();

    public abstract void update(String table, DataKey dataKey, String column, Object data);
    public abstract DataValue getData(String table, DataKey dataKey, String column);
    public abstract Map<String, DataValue> getRow(String table, DataKey dataKey);
    public abstract Map<String, DataValue> getRow(String table, int row);

    public abstract void send(String query, Object... params) throws Exception;
    public abstract List<Map<String, DataValue>> sendAndRead(String query, Object... params) throws Exception;
    public abstract void close();
    
    private Map<String, ISubDatabase> sub = new HashMap<>();
    
    public void registerSub(ISubDatabase subDatabase) {
        subDatabase.setParent(this);
        this.sub.put(subDatabase.getTable(), subDatabase);
    }
    
    public ISubDatabase getSub(String sub) {
        return this.sub.get(sub);
    }
}
