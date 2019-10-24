package me.ImSpooks.core.helpers;

import lombok.Getter;

/**
 * Created by Nick on 22 okt. 2019.
 * Copyright © ImSpooks
 */
public class ThreadBuilder {

    @Getter private Thread thread;

    public ThreadBuilder(Thread thread) {
        this.thread = thread;
    }

    public ThreadBuilder(Runnable runnable) {
        this.thread = new Thread(runnable);
    }

    public ThreadBuilder(Runnable runnable, String name) {
        this.thread = new Thread(runnable, name);
    }


    public ThreadBuilder(Runnable runnable, int priority) {
        this.thread = new Thread(runnable);
        this.thread.setPriority(priority);
    }


    public ThreadBuilder(Runnable runnable, String name, int priority) {
        this.thread = new Thread(runnable, name);
        this.thread.setPriority(priority);
    }

    public ThreadBuilder start() {
        this.thread.start();
        return this;
    }
}
