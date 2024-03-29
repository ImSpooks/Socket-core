package me.ImSpooks.core.server.connection;

import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.helpers.ThreadBuilder;

import java.util.*;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class ConnectionMaps {

    public static final Set<String> blockedHosts = Collections.synchronizedSet(new HashSet<>());
    public static final Map<Long, Integer> connectionAttempts = new HashMap<>();

    static {
        new ThreadBuilder(() -> {
            while (true) {
                JavaHelpers.sleep(1000 * 60 * 5);
                blockedHosts.clear();
                connectionAttempts.clear();
            }
        }, "Clear blocked hosts Thread", Thread.MIN_PRIORITY).start();
    }
}
