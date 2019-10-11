package me.ImSpooks.core.server.database.sql;

import me.ImSpooks.core.common.database.DataKey;
import me.ImSpooks.core.server.database.IDatabase;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08 okt. 2019.
 * Copyright © ImSpooks
 */
public class DatabaseSql extends IDatabase {

    private Connection connection;
    private DatabaseMetaData connectionData;

    public DatabaseSql(String target, int port, String username, String password, String database) throws Exception {
        super(target, port, username, password, database);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", this.getTarget(), this.getPort(), this.getDatabase()), this.getUsername(), this.getPassword());
            this.connectionData = this.connection.getMetaData();

        } catch (SQLException e) {
            throw new SQLException(String.format("Could not connect to mysql database with [target: %s, username: %s, password: %s, database:%s]",
                    this.getTarget(), this.getUsername(), this.getPassword(), this.getDatabase()), e);
        }
    }

    @Override
    public void update(String collection, DataKey key, String column, Object data) throws SQLException {
        this.sendUpdate(
                String.format("UPDATE `%s` SET `%s` = ? WHERE `%s` = ?", collection, column, key.getKey()),
                collection,
                column,
                data,
                key.getValue()
        );
    }

    @Override
    public Object getData(String collection, DataKey key, String column) throws SQLException {
        ResultSet rs = this.connectionData.getColumns(null, null, collection, column);
        if (!rs.next()) {
            rs.close();
            return null;
        }

        return this.getData(collection, key).get(column);
    }

    @Override
    public Document getData(String collection, DataKey key) throws SQLException {
        ResultSet rs = this.connectionData.getTables(null, null, collection, new String[] {"TABLE"});
        if (!rs.next()) {
            return new Document();
        }

        Document document = new Document();

        ResultSet resultSet = this.send(String.format("SELECT * FROM `%s` WHERE `%s` = ?", collection, key.getKey()), key.getValue());
        ResultSetMetaData metaData = resultSet.getMetaData();
        if (resultSet.next()) {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                document.append(metaData.getColumnName(i), resultSet.getObject(i));
            }
        }
        resultSet.close();

        return document;
    }

    @Override
    public List<Document> getCollection(String collection) throws SQLException {
        ResultSet rs = this.connectionData.getTables(null, null, collection, new String[] {"TABLE"});
        if (!rs.next())
            return new ArrayList<>();

        List<Document> documents = new ArrayList<>();

        ResultSet resultSet = this.send(String.format("SELECT * FROM `%s`", collection));
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            Document document = new Document();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                document.append(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
            }
            documents.add(document);
        }
        resultSet.close();

        return documents;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

    private ResultSet send(String query, Object... params) throws SQLException {
        return this.query(query, false, params);
    }

    private ResultSet sendUpdate(String query, String table, String column, Object... params) throws SQLException {
        if (query.startsWith("UPDATE")) {
            if (params.length == 2) {
                Object value = params[0];
                ResultSet set = this.connectionData.getColumns(null, null, table, column);
                if (!set.next()) {
                    // column doesnt exist;

                    SqlDataTypes type;
                    if (value instanceof String &&
                            ((((String) value).startsWith("{") && ((String) value).endsWith("}")) ||
                            (((String) value).startsWith("[") && ((String) value).endsWith("]")))
                    ) type = SqlDataTypes.JSON;
                    else type = SqlDataTypes.getFromClass(value.getClass());

                    this.query(String.format("ALTER TABLE `%s` ADD COLUMN %s %s", table, column, type.name()), true);
                }
                set.close();
            }
        }

        return this.query(query, true, params);
    }

    private ResultSet query(String query, boolean update, Object... params) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query);

        for (int i = 0; i < params.length; i++) {
            Object value = params[i];

            if (value instanceof String)            statement.setString(i + 1, (String) value);
            else if (value instanceof Integer)      statement.setInt(i + 1, (Integer) value);
            else if (value instanceof Long)         statement.setLong(i + 1, (Long) value);
            else if (value instanceof Float)        statement.setFloat(i + 1, (Float) value);
            else if (value instanceof Double)       statement.setDouble(i + 1, (Double) value);
            else if (value instanceof Character)    statement.setString(i + 1, String.valueOf(value));
            else                                    statement.setObject(i + 1, value);
        }

        if (!update) {
            ResultSet set = statement.executeQuery();
            return set;
        }
        else {
            statement.executeUpdate();
            return null;
        }
    }
}
