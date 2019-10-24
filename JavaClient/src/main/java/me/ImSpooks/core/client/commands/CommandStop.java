package me.ImSpooks.core.client.commands;

import me.ImSpooks.core.client.CoreClient;
import me.ImSpooks.core.common.commands.CommandExecutor;
import me.ImSpooks.core.common.interfaces.CoreImplementation;
import me.ImSpooks.core.packets.collection.network.PacketClosing;

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
        if (args.length == 1 && args[0].equalsIgnoreCase("-9")) {
            System.out.println("Terminating client.");
            ((CoreClient) core).getClient().write(new PacketClosing());
            System.exit(1);
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
        return "Stop the clients instance";
    }

    @Override
    public String getUsage() {
        return "stop [-9]";
    }
}
