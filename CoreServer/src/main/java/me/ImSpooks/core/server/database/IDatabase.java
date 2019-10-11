package me.ImSpooks.core.server.database;

import lombok.Getter;
import me.ImSpooks.core.common.database.DataKey;
import org.bson.Document;

import java.util.List;

/**
 * Created by Nick on 08 okt. 2019.
 * Copyright © ImSpooks
 */
public abstract class IDatabase {

    @Getter private String target;
    @Getter private String username;
    @Getter private int port;
    @Getter private String password;
    @Getter private String database;

    public IDatabase(String target, int port, String username, String password, String database) {
        this.target = target;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public abstract void update(String collection, DataKey key, String column, Object data) throws Exception;
    public abstract Object getData(String collection, DataKey key, String column) throws Exception;
    public abstract Document getData(String collection, DataKey key) throws Exception;
    public abstract List<Document> getCollection(String collection) throws Exception;

    public abstract void close() throws Exception;
}
