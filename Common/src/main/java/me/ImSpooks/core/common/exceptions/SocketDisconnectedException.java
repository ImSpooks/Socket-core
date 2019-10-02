package me.ImSpooks.core.common.exceptions;

import java.io.IOException;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class SocketDisconnectedException extends IOException {

    public SocketDisconnectedException() {
    }

    public SocketDisconnectedException(String message) {
        super(message);
    }

    public SocketDisconnectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketDisconnectedException(Throwable cause) {
        super(cause);
    }
}
