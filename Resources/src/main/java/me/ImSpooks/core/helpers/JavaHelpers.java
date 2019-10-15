package me.ImSpooks.core.helpers;

import org.tinylog.Logger;

/**
 * Created by Nick on 27 sep. 2019.
 * Copyright © ImSpooks
 */
public class JavaHelpers {

    public static void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            Logger.warn("Something went wrong while trying to sleep thread \'{}\'", Thread.currentThread().getName());
        }
    }

    public static void sleep(int milis) {
        JavaHelpers.sleep((long) milis);
    }
}
