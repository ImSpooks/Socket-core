package me.ImSpooks.core.server;

import lombok.Getter;
import me.ImSpooks.core.commands.CommandStop;
import me.ImSpooks.core.common.commands.CommandManager;
import me.ImSpooks.core.common.interfaces.CoreImplementation;
import me.ImSpooks.core.common.json.JSONConfig;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.packets.handler.PacketHandler;
import me.ImSpooks.core.server.database.DatabaseJson;
import me.ImSpooks.core.server.database.IDatabase;
import me.ImSpooks.core.server.database.mongodb.DatabaseMongodb;
import me.ImSpooks.core.server.database.sql.DatabaseSql;
import me.ImSpooks.core.server.init.Server;
import org.tinylog.Logger;

/**
 * Created by Nick on 27 sep. 2019.
 * Copyright © ImSpooks
 */
public class CoreServer implements CoreImplementation {

    @Getter private Server server;
    @Getter private final String password;

    @Getter private PacketHandler packetHandler;
    @Getter private IDatabase database;
    @Getter private CommandManager commandManager;

    public CoreServer(String password, int port) {

        this.password = password;

        this.packetHandler = new PacketHandler(this, this.password);
        this.server = new Server(port, this);


        JSONConfig config = new JSONConfig("config", "database.json");

        config.expect("type", "MYSQL");
        config.expect("target", "127.0.0.1");
        config.expect("port", 3306);
        config.expect("username", "java");
        config.expect("password", "INSERT HERE");
        config.expect("database", "INSERT HERE");

        Logger.info("Connecting to database...");
        try {
            if (config.getString("type").equalsIgnoreCase("MYSQL")) {
                this.database = new DatabaseSql(
                        config.getString("target"),
                        config.getInt("port"),
                        config.getString("username"),
                        config.getString("password"),
                        config.getString("database")
                );
            }
            else if (config.getString("type").equalsIgnoreCase("MONGODB")) {
                this.database = new DatabaseMongodb(
                        config.getString("target"),
                        config.getInt("port"),
                        config.getString("username"),
                        config.getString("password"),
                        config.getString("database")
                );
            }
            else if (config.getString("type").equalsIgnoreCase("JSON")) {
                this.database = new DatabaseJson(config.getString("database"));
            }
        } catch (Exception e) {
            Logger.error(e);
            Logger.info("An exception was thrown while connecting to a {} database, creating a json data file instead.", config.getString("type").toLowerCase());
            this.database = new DatabaseJson(config.getString("database"));
        }

        if (this.database != null) {
            Logger.info("Connected to {} database ", config.getString("type").toLowerCase());
        }
        else {
            Logger.error("No database type found, exiting...");
            System.exit(-1);
        }

        this.commandManager = new CommandManager(System.in, this);
        this.commandManager.registerCommand(new CommandStop(this));
    }

    @Override
    public void stop() {
        this.shutdownHook().run();
        System.exit(0);
    }

    @Override
    public Runnable shutdownHook() {
        return () -> {
            // waiting for tinylogger
            JavaHelpers.sleep(10);
            Logger.info("Shutting down server...");
            server.close();
            try {
                database.close();
            } catch (Exception e) {
                Logger.warn(e, "An exception was thrown while closing the database connection.");
            }
            Logger.info("Server successfully shut down.");
        };
    }
}
