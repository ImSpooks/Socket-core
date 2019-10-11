package me.ImSpooks.core.server.database.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import me.ImSpooks.core.common.database.DataKey;
import me.ImSpooks.core.server.database.IDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nick on 09 okt. 2019.
 * Copyright © ImSpooks
 */
public class DatabaseMongodb extends IDatabase {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public DatabaseMongodb(String target, int port, String username, String password, String database) throws Exception {
        super(target, port, username, password, database);

        this.mongoClient = new MongoClient(
                new ServerAddress(target, port),
                MongoCredential.createCredential(this.getUsername(), this.getDatabase(), this.getPassword().toCharArray()),
                MongoClientOptions.builder().build());
        this.database = this.mongoClient.getDatabase(this.getDatabase());
    }

    @Override
    public void update(String collection, DataKey key, String column, Object data) {
        MongoCollection<?> coll = this.database.getCollection(collection);
        Document document = this.getData(collection, key);
        Document update = new Document().append(key.getKey(), data);
        coll.updateMany(document, new Document("$set", update));
    }

    @Override
    public Object getData(String collection, DataKey key, String column) {
        Document data = this.getData(collection, key);
        if (data != null)
            return data.get(column);
        return null;
    }

    @Override
    public Document getData(String collection, DataKey key) {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        MongoCursor<Document> cursor = coll.find(new Document(key.getKey(), key.getValue())).iterator();
        if (cursor.hasNext())
            return cursor.next();
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Document> getCollection(String collection) throws Exception {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        return new ArrayList<>((Collection<? extends Document>) coll.find());
    }

    @Override
    public void close() {
        this.mongoClient.close();
    }
}
