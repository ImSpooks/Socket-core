package me.ImSpooks.core.common.commands;

import me.ImSpooks.core.common.interfaces.CoreImplementation;
import me.ImSpooks.core.helpers.ThreadBuilder;
import org.tinylog.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nick on 24 okt. 2019.
 * Copyright © ImSpooks
 */
public class CommandManager {
    private final CoreImplementation core;

    private List<CommandExecutor> commands;

    private boolean shouldRun = true;

    public CommandManager(InputStream inputStream, CoreImplementation core){
        this.core = core;
        this.commands = new ArrayList<>();

        new ThreadBuilder(() -> {
            try (Scanner scanner = new Scanner(inputStream)) {
                while (shouldRun && scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    this.callCommand(line);
                }
            }
        }, "Command Handler", 2).start();
    }

    public void registerCommand(CommandExecutor... commandExecutor) {
        this.commands.addAll(Arrays.asList(commandExecutor));

    }

    private CommandExecutor findByName(String name) {
        for (CommandExecutor e : this.commands) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    private CommandExecutor findByClass(Class clazz) {
        for (CommandExecutor e : this.commands) {
            if (e.getClass().getName().equalsIgnoreCase(clazz.getName())) {
                return e;
            }
        }
        return null;
    }

    public void callCommand(String input){
        input = input.trim();
        if (input.isEmpty())
            return;

        String[] split = input.split(" ");
        String commandInput = split[0];
        String[] args = Arrays.stream(input.substring(commandInput.length()).trim().split(" ")).filter(x -> !x.trim().isEmpty()).toArray(String[]::new);


        if (commandInput.equalsIgnoreCase("help")) {
            if (args.length == 2) {
                try {
                    int page = Integer.parseInt(args[0]);
                    if (page >= 1) {
                        this.showHelp((page - 1));
                    } else {
                        this.showHelp(0);
                    }
                } catch (NumberFormatException e) {
                    this.showHelp(0);
                }
            } else {
                this.showHelp( 0);
            }
            return;
        }

        CommandExecutor command = this.findByName(commandInput);
        if (command == null) {
            Logger.info("The following command \"{}\" is not found, use -help for all valid commands.", commandInput);
            return;
        }

        try {
            if (!command.onCommand(args)) {
                Logger.info("Command returned false");
            }
        } catch (Exception e) {
            Logger.warn(e);
        }
    }

    public void showHelp(int page) {
        Logger.info("Available commands:");

        int maxPage = (int) Math.ceil(this.commands.size() / 4.0);
        if (page <= maxPage) {
            for (int i = (page * 4); i < (page * 4) + 4; i++) {
                if (commands.size() > i) {
                    CommandExecutor executor = this.commands.get(i);
                    Logger.info("\t• {}; {}", executor.getUsage(), executor.getDescription());
                }
            }
        } else {
            page = 0;
            for (int i = (page * 4); i < (page * 4) + 4; i++) {
                if (commands.size() > i) {
                    CommandExecutor executor = this.commands.get(i);
                    Logger.info("\t• {}; {}", executor.getUsage(), executor.getDescription());
                }
            }
        }

        Logger.info("");
        if (page != maxPage) {
            Logger.info("Type \'{}\' to go to the next page.", String.format("help %s", page + 2));
        } else {
            Logger.info("Type \'{}\' to go back to the first page.", "help 1");
        }
    }

    public void stop() {
        this.shouldRun = false;
    }
}
