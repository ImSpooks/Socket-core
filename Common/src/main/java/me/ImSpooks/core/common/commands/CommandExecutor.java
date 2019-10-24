package me.ImSpooks.core.common.commands;

import me.ImSpooks.core.common.interfaces.CoreImplementation;

/**
 * Created by Nick on 24 okt. 2019.
 * Copyright © ImSpooks
 */
public abstract class CommandExecutor {

    protected final CoreImplementation core;
    public CommandExecutor(CoreImplementation core) {
        this.core = core;
    }

    public abstract boolean onCommand(String[] args);
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsage();
}
