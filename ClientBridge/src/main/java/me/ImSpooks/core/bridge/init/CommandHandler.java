package me.ImSpooks.core.bridge.init;

import lombok.Getter;

/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */
public class CommandHandler {

    private final String input;
    @Getter private String output = "";

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