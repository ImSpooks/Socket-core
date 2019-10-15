package me.ImSpooks.core.bridge.init;

import lombok.Getter;
import me.ImSpooks.core.packets.init.Packet;

/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */
public class CommandHandler {

    private final String input;
    @Getter private Packet packet;

    public CommandHandler(String input) {
        this.input = input;
    }

    public boolean handleCommand() {
        boolean returnOutput = false;

        switch (input) {
            default:
                break;
        }

        return returnOutput;
    }
}