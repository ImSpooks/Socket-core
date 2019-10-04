package me.ImSpooks.core.database.sql;

import me.ImSpooks.core.database.IDatabase;
import me.ImSpooks.core.database.data.CachedData;
import me.ImSpooks.core.database.data.DataKey;
import me.ImSpooks.core.database.data.DataValue;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 03 okt. 2019.
 * Copyright © ImSpooks
 */
public class DatabaseMySQL extends IDatabase {

    private DataKey dataKey;
    private Connection connection;

    public DatabaseMySQL(String target, String username, String password, String database) {
        super(target, username, password, database);
        // 8JYNIdkEeF7zNBLf

        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:3306/%s", this.getTarget(), this.getDatabase()),"root","root");

            this.intialize();
        } catch (SQLException e) {
            Logger.warn(e, "Could not connect to mysql database with [target: {}, username: {}, password: {}, database:{}]",
                    this.getTarget(), this.getUsername(), this.getPassword(), this.getDatabase());
        } catch (ClassNotFoundException e) {
            Logger.warn(e, "MySQL driver not found.");
        }
    }

    private List<String> tables = new ArrayList<>();

    @Override
    public void update(String table, DataKey dataKey, String column, Object data) {
        Map<String, Object[]> queries = new LinkedHashMap<>();

        // table doesnt exist
        if (!this.cachedData.containsKey("table")) {
            tables.add(table);
            this.cachedData.put(table, new CachedData(new LinkedHashMap<>()));

            queries.put("CREATE TABLE ? (id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY)", new Object[] {table});
        }

        // column doesnt exist
        if (!this.cachedData.get(table).containsColumn(column)) {
            for (Map<String, DataValue> stringDataValueMap : this.cachedData.get(table).get()) {
                stringDataValueMap.put(column, null);
            }
            queries.put("ALTER TABLE ? ADD COLUMN ? ?", new Object[] {table, column, SqlDataTypes.getFromClass(data.getClass()).getSqlType()});
        }

        // row doesnt exist
        if (this.cachedData.get(table).getRow(dataKey) == null) {
            queries.put("INSERT INTO ? (?) VALUES (?)", new Object[] {table, dataKey.getKey(), dataKey});
            this.cachedData.get(table).addNew(dataKey);
        }


        this.cachedData.get(table).update(dataKey, column, new DataValue(data));
        queries.put("UPDATE ? SET ? = ? WHERE ? = ?", new Object[] {table, column, data, dataKey.getKey(), dataKey});

        for (Map.Entry<String, Object[]> entry : queries.entrySet()) {
            try {
                this.send(entry.getKey(), entry.getValue());
            } catch (SQLException e) {
                Logger.error(e, "Something went wrong while updating sql data");
            }
        }
    }

    @Override
    public DataValue getData(String table, DataKey dataKey, String column) {
        return this.cachedData.get(table).getColumn(dataKey,  column);
    }

    @Override
    public Map<String, DataValue> getRow(String table, DataKey dataKey) {
        return this.cachedData.get(table).getRow(dataKey);
    }

    @Override
    public Map<String, DataValue> getRow(String table, int row) {
        return (Map<String, DataValue>) this.cachedData.get(table).get().get(row);
    }

    @Override
    public void send(String query, Object... params) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query);

        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params);
        }

        ResultSet resultSet = statement.executeQuery();
        statement.close();
        resultSet.close();
    }

    @Override
    public List<Map<String, DataValue>> sendAndRead(String query, Object... params) throws SQLException {
        List<Map<String, DataValue>> result = new ArrayList<>();

        PreparedStatement statement = this.connection.prepareStatement(query);

        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params);
        }

        ResultSet resultSet = statement.executeQuery();
        
        ResultSetMetaData metaData = resultSet.getMetaData();

        while(resultSet.next()) {
            Map<String, DataValue> resultMap = new LinkedHashMap<>();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                resultMap.put(resultSet.getCursorName(), new DataValue(resultSet.getObject(i)));
            }

            result.add(resultMap);
        }

        statement.close();
        resultSet.close();

        return result;
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            Logger.error(e, "Something went wrong while trying to close the mysql connection");
        }
    }

    private void intialize() throws SQLException{
        DatabaseMetaData md = this.connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            String table = rs.getString(3);

            this.tables.add(table);

            try {
                List<Map<String, DataValue>> result = this.sendAndRead("SELECT * FROM ?", table);
                this.cachedData.put(table, new CachedData(result));
            } catch (SQLException e) {
                throw new SQLException("Something went wrong while caching all data", e);
            }
        }
    }
}
