package me.ImSpooks.core.server.database;

import com.google.gson.JsonIOException;
import lombok.Getter;
import me.ImSpooks.core.common.database.DataKey;
import me.ImSpooks.core.common.json.JSONConfig;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 11 okt. 2019.
 * Copyright © ImSpooks
 */

public class DatabaseJson extends IDatabase {

    private final String directory;
    private final Map<String, JSONConfig> configMap;
    @Getter private boolean closed = false;

    public DatabaseJson(String database) {
        super("", -1, "", "", database);

        this.directory = this.getDatabase();
        this.configMap = new HashMap<>();
        //this.config = new JSONConfig(dir.substring(0, dir.length() - 1), fileName + ".json");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(String collection, DataKey key, String column, Object data) {
        if (this.closed)
            throw new JsonIOException("json config closed");

        JSONConfig config = this.get(collection);

        JSONArray<JSONObject> array = config.getJsonArray("data");

        boolean found = false;
        for (JSONObject jsonObject : array) {
            Object type = jsonObject.get(key.getKey());
            Object compareType = key.getValue();

            // in case if the key value is a long and comparing to an integer
            if (type instanceof Long && compareType instanceof Integer) {
                compareType = (long) (Integer) compareType;
            }

            if (type.equals(compareType)) {
                if (data != null)
                    jsonObject.put(column, data);
                else jsonObject.remove(column);
                found = true;
                break;
            }
        }
        if (!found) {
            JSONObject object = new JSONObject();
            object.put(key.getKey(), key.getValue());
            assert data != null;
            object.put(column, data);
            array.add(object);
        }

        config.save();
    }

    @Override
    public Object getData(String collection, DataKey key, String column) {
        return this.getData(collection, key).get(column);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Document getData(String collection, DataKey key) {
        if (this.closed)
            throw new JsonIOException("json config closed");

        JSONConfig config = this.get(collection);

        Document document = new Document();

        if (!config.getArray("data").isEmpty()) {
            JSONArray<JSONObject> array = config.getJsonArray("data");

            for (JSONObject jsonObject : array) {
                if (jsonObject.get(key.getKey()).equals(key.getValue())) {
                    jsonObject.forEach((key1, value) -> document.append(String.valueOf(key1), value));
                }
            }
        }

        return document;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Document> getCollection(String collection) {
        if (this.closed)
            throw new JsonIOException("json config closed");

        JSONConfig config = this.get(collection);

        List<Document> documents = new ArrayList<>();

        if (!config.getArray("data").isEmpty()) {
            JSONArray<JSONObject> array = config.getJsonArray("data");

            for (JSONObject jsonObject : array) {
                Document document = new Document();

                jsonObject.forEach((key, value) -> document.append(String.valueOf(key), value));

                documents.add(document);
            }
        }
        return documents;
    }

    @Override
    public void close() {
        this.configMap.values().forEach(JSONConfig::save);
        this.configMap.clear();
        this.closed = true;
    }

    private JSONConfig get(String collection) {
        this.configMap.putIfAbsent(collection, new JSONConfig(this.directory, collection + ".json"));
        this.configMap.get(collection).expect("data", new JSONArray<>());
        return this.configMap.get(collection);
    }
}
