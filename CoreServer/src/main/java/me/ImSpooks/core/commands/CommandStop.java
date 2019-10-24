package me.ImSpooks.core.commands;

import me.ImSpooks.core.common.commands.CommandExecutor;
import me.ImSpooks.core.common.interfaces.CoreImplementation;
import me.ImSpooks.core.packets.collection.network.PacketClosing;
import me.ImSpooks.core.packets.collection.network.PacketStop;
import me.ImSpooks.core.server.CoreServer;
import me.ImSpooks.core.server.init.ServerClient;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Nick on 24 okt. 2019.
 * Copyright © ImSpooks
 */
public class CommandStop extends CommandExecutor {

    public CommandStop(CoreImplementation core) {
        super(core);
    }

    @Override
    public boolean onCommand(String[] args) {
        if (args.length > 0) {
            CoreServer server = (CoreServer) core;
            if (args[0].equalsIgnoreCase("-9")) {
                System.out.println("Terminating server.");
                ((CoreServer) core).getServer().getClients().keySet().forEach(client -> client.write(new PacketClosing()));
                System.exit(1);
            }
            else {
                Optional<ServerClient> clientOptional = server.getServer().getClients().keySet().stream().filter(serverClient -> serverClient.getClientName().equalsIgnoreCase(args[0])).findFirst();
                if (!clientOptional.isPresent()) {
                    Logger.info("No client found with the name \"{}\"", args[0]);
                    return true;
                }
                ServerClient client = clientOptional.get();
                Logger.info("Shutting down client \"{}\"", client.getClientName());

                client.setClosed(true);
                client.write(new PacketStop());
                try {
                    client.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        core.stop();
        return true;
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stop the server's or a specific client's instance";
    }

    @Override
    public String getUsage() {
        return "stop [client name | -9]";
    }
}
